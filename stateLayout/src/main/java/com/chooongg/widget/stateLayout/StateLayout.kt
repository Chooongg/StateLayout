package com.chooongg.widget.stateLayout

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.AbsSavedState
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.chooongg.widget.stateLayout.animate.StateAnimate
import com.chooongg.widget.stateLayout.state.AbstractState
import com.chooongg.widget.stateLayout.state.ContentState
import com.google.android.material.appbar.AppBarLayout
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

open class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    beginIsContent: Boolean = false,
    enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), NestedScrollingChild3,
    NestedScrollingParent3 {

    private val childHelper = NestedScrollingChildHelper(this)
    private val parentHelper = NestedScrollingParentHelper(this)

    /**
     * 当前状态
     */
    var currentState: KClass<out AbstractState> = ContentState::class
        private set

    /**
     * 当前状态的参数
     */
    private var currentStateParam: Any? = null

    /**
     * 上一个状态
     */
    private var preState: KClass<out AbstractState> = ContentState::class

    /**
     * 上一个状态的参数
     */
    private var preStateParam: Any? = null

    /**
     * 是否启用动画
     */
    var isEnableAnimate: Boolean = enableAnimation

    /**
     * 动画实现类
     */
    var animate: StateAnimate = StateLayoutManager.animate

    /**
     * 重试操作监听
     */
    private var onRetryEventListener: OnRetryEventListener? = null

    /**
     * 状态变化监听
     */
    private var onStateChangedListener: OnStateChangedListener? = null

    private var appBarLayout: WeakReference<AppBarLayout>? = null

    private var liftOnScrollTargetViewId: Int = 0

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.StateLayout, defStyleAttr, defStyleRes
        )
        val isContent = a.getBoolean(R.styleable.StateLayout_beginIsContent, beginIsContent)
        if (!isInEditMode && !isContent && StateLayoutManager.beginState != ContentState::class) {
            showInternal(StateLayoutManager.beginState, null, false)
        }
        a.recycle()
        childHelper.isNestedScrollingEnabled = true
    }

    /**
     * show content state
     */
    fun showContent() = show(ContentState::class)

    /**
     * show state
     */
    fun show(stateClass: KClass<out AbstractState>, param: Any? = null) {
        post { showInternal(stateClass, param, isEnableAnimate) }
    }

    /**
     * show status internal implementation
     */
    private fun showInternal(
        stateClass: KClass<out AbstractState>, param: Any? = null, isAnimate: Boolean
    ) {
        if (stateClass == currentState) {
            forEach { if (it::class == stateClass) (it as AbstractState).onChangeParam(param) }
            return
        }
        setOnClickListener(null)
        val stateIsHas = children.find { it::class == stateClass }
        if (stateIsHas == null && stateClass != ContentState::class) createOtherState(stateClass)
        forEach {
            if (stateClass == ContentState::class) {
                showContentState(it, isAnimate)
                hideOtherState(it, isAnimate)
            } else {
                showOtherState(it, stateClass, param, isAnimate)
            }
        }
        preState = currentState
        preStateParam = currentStateParam
        currentState = stateClass
        currentStateParam = param

        if (currentState == ContentState::class) {
            setAppBarLayoutTargetView(true)
            onStateChangedListener?.onStateChanged(ContentState::class, true)
        } else {
            val childView = children.find { it::class == currentState && it is AbstractState }
            if (childView != null) {
                val state = childView as AbstractState
                setAppBarLayoutTargetView(state.isMeanwhileContent())
                onStateChangedListener?.onStateChanged(
                    state::class, childView.isMeanwhileContent()
                )
            } else {
                setAppBarLayoutTargetView(true)
                onStateChangedListener?.onStateChanged(
                    ContentState::class, true
                )
            }
        }
    }

    internal fun showPrevious(param: Any? = preStateParam) {
        showInternal(preState, param, isEnableAnimate)
    }

    private fun showContentState(view: View, isAnimate: Boolean) {
        val lp = view.layoutParams
        if (lp !is LayoutParams) return
        if (lp.isStateView) return
        when (lp.visibilityStrategy) {
            // 如果是Content状态显示策略，那么就显示
            LayoutParams.CONTENT -> if (isAnimate && canUseAnimate()) {
                if (view.visibility != View.VISIBLE) animate.createAnimate(view)
                animate.showAnimate(view) { view.visibility = View.VISIBLE }
            } else {
                animate.resetAnimate(view)
                view.visibility = View.VISIBLE
            }
            // 如果是其他状态显示策略，那么就隐藏
            LayoutParams.OTHER -> if (isAnimate && canUseAnimate()) {
                animate.hideAnimate(view) { view.visibility = View.GONE }
            } else view.visibility = View.GONE

            LayoutParams.OTHER_IGNORE_CONTENT -> if (currentState == ContentState::class) {
                if (isAnimate && canUseAnimate()) {
                    if (view.visibility != View.VISIBLE) animate.createAnimate(view)
                    animate.showAnimate(view) { view.visibility = View.VISIBLE }
                } else {
                    animate.resetAnimate(view)
                    view.visibility = View.VISIBLE
                }
            } else {
                if (isAnimate && canUseAnimate()) {
                    animate.hideAnimate(view) { view.visibility = View.GONE }
                } else view.visibility = View.GONE
            }
        }
    }

    private fun hideContentState(view: View, isAnimate: Boolean) {
        val lp = view.layoutParams
        if (lp !is LayoutParams) return
        if (lp.isStateView) return
        when (lp.visibilityStrategy) {
            // 如果是Content状态显示策略，那么就隐藏
            LayoutParams.CONTENT -> if (isAnimate && canUseAnimate()) {
                animate.hideAnimate(view) { view.visibility = View.GONE }
            } else view.visibility = View.GONE
            // 如果是其他状态显示策略，那么就显示
            LayoutParams.OTHER -> if (isAnimate && canUseAnimate()) {
                if (view.visibility != View.VISIBLE) animate.createAnimate(view)
                animate.showAnimate(view) { view.visibility = View.VISIBLE }
            } else {
                animate.resetAnimate(view)
                view.visibility = View.VISIBLE
            }
            // 如果是其他状态显示策略，那么就显示(忽略是否显示内容)
            LayoutParams.OTHER_IGNORE_CONTENT -> if (currentState == ContentState::class) {
                if (isAnimate && canUseAnimate()) {
                    if (view.visibility != View.VISIBLE) animate.createAnimate(view)
                    animate.showAnimate(view) { view.visibility = View.VISIBLE }
                } else {
                    animate.resetAnimate(view)
                    view.visibility = View.VISIBLE
                }
            } else {
                if (isAnimate && canUseAnimate()) {
                    animate.hideAnimate(view) { view.visibility = View.GONE }
                } else view.visibility = View.GONE
            }
        }
    }

    private fun createOtherState(stateClass: KClass<out AbstractState>): AbstractState {
        val constructor = stateClass.java.getConstructor(Context::class.java)
        val state = constructor.newInstance(context)
        addView(state, state.generateLayoutParams())
        state.getRetryEventView()?.setOnClickListener {
            if (isEnabled) onRetryEventListener?.onStateRetry(stateClass)
        }
        return state
    }

    private fun showOtherState(
        view: View, stateClass: KClass<out AbstractState>, param: Any?, isAnimate: Boolean
    ) {
        if (view::class == stateClass) {
            val stateView = (view as AbstractState)
            stateView.onChangeParam(param)
            if (isAnimate && stateView.isShowAnimate() && canUseAnimate()) {
                if (stateView.visibility != View.VISIBLE) animate.createAnimate(stateView)
                animate.showAnimate(stateView) { stateView.visibility = View.VISIBLE }
            } else {
                animate.resetAnimate(stateView)
                stateView.visibility = View.VISIBLE
            }
            forEach {
                if (stateView.isMeanwhileContent()) showContentState(it, isAnimate)
                else hideContentState(it, isAnimate)
            }
        } else hideOtherState(view, isAnimate)
    }

    internal fun hideOtherState(view: View, isAnimate: Boolean, endBlock: (() -> Unit)? = null) {
        val lp = view.layoutParams
        if (lp !is LayoutParams) return
        if (!lp.isStateView) return
        val stateView = (view as AbstractState)
        if (isAnimate && stateView.isHideAnimate() && canUseAnimate()) {
            animate.hideAnimate(stateView) {
                removeView(stateView)
                endBlock?.invoke()
            }
        } else {
            removeView(stateView)
            endBlock?.invoke()
        }
    }


    internal fun canUseAnimate() = isAttachedToWindow && isVisible && isEnabled

    /**
     * 设置重试操作监听
     */
    fun setOnRetryEventListener(block: ((KClass<out AbstractState>) -> Unit)) {
        onRetryEventListener = OnRetryEventListener(block)
    }

    /**
     * 设置重试操作监听
     */
    fun setOnRetryEventListener(listener: OnRetryEventListener?) {
        onRetryEventListener = listener
    }

    /**
     * 设置状态变化监听
     */
    fun setOnStateChangedListener(block: ((currentState: KClass<out AbstractState>, contentIsShow: Boolean) -> Unit)) {
        onStateChangedListener = OnStateChangedListener(block)
    }

    /**
     * 设置状态变化监听
     */
    fun setOnStateChangedListener(listener: OnStateChangedListener?) {
        onStateChangedListener = listener
    }

    fun bindAppBarLayoutLiftOnScroll(appBarLayout: AppBarLayout, @IdRes targetViewId: Int) {
        this.appBarLayout = WeakReference(appBarLayout)
        liftOnScrollTargetViewId = targetViewId
        if (currentState == ContentState::class) {
            setAppBarLayoutTargetView(true)
        } else {
            val state = children.find { it::class == currentState && it is AbstractState }
            if (state != null) {
                setAppBarLayoutTargetView((state as AbstractState).isMeanwhileContent())
            } else setAppBarLayoutTargetView(false)
        }
    }

    private fun setAppBarLayoutTargetView(isTargetViewId: Boolean) {
        appBarLayout?.get()?.let {
            if (isTargetViewId && liftOnScrollTargetViewId != 0) {
                it.liftOnScrollTargetViewId = liftOnScrollTargetViewId
            } else it.setLiftOnScrollTargetView(this)
        }
    }

    @Suppress("unused")
    protected open fun onSetLayoutParams(child: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkChildViewUpdateIsNeedRequestLayout(child, layoutParams)
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState(super.onSaveInstanceState())
        state.currentStateClassName = currentState.java.name
        state.preStateClassName = preState.java.name
        return state
    }

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        if (state.currentStateClassName != null) {
            val temp = onStateChangedListener
            onStateChangedListener = null
            showInternal(
                Class.forName(state.currentStateClassName!!).kotlin as KClass<out AbstractState>,
                null,
                false
            )
            onStateChangedListener = temp
        }
        if (state.preStateClassName != null) {
            preState = Class.forName(state.preStateClassName!!).kotlin as KClass<out AbstractState>
        }
    }

    override fun generateDefaultLayoutParams() =
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)
    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        when (lp) {
            null -> generateDefaultLayoutParams()
            is LayoutParams -> LayoutParams(lp)
            is FrameLayout.LayoutParams -> LayoutParams(lp)
            is MarginLayoutParams -> LayoutParams(lp)
            else -> LayoutParams(lp)
        }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        val temp = params as? LayoutParams ?: (generateLayoutParams(params) as LayoutParams)
        if (child != null) {
            temp.isStateView = child is AbstractState
            checkChildViewUpdateIsNeedRequestLayout(child, temp)
        }
        super.addView(child, index, params)
    }

    /**
     * 检查子View是否需要更新布局
     */
    private fun checkChildViewUpdateIsNeedRequestLayout(
        child: View?, layoutParams: ViewGroup.LayoutParams?
    ) {
        if (isInEditMode) return
        if (child == null) return
        if (layoutParams !is LayoutParams) return
        when {
            layoutParams.isStateView -> child.visibility =
                if (currentState == child::class) View.VISIBLE else View.GONE

            layoutParams.visibilityStrategy == LayoutParams.CONTENT -> child.visibility =
                if (currentState == ContentState::class) View.VISIBLE else View.GONE

            layoutParams.visibilityStrategy == LayoutParams.OTHER -> child.visibility =
                if (currentState == ContentState::class) View.GONE else View.VISIBLE

            layoutParams.visibilityStrategy == LayoutParams.OTHER_IGNORE_CONTENT -> child.visibility =
                if (currentState == ContentState::class) View.GONE else View.VISIBLE
        }
    }

    class LayoutParams : FrameLayout.LayoutParams {

        internal var isStateView: Boolean = false

        @ShowStrategy
        var visibilityStrategy: Int = CONTENT

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.StateLayout_Layout)
            visibilityStrategy =
                a.getInt(R.styleable.StateLayout_Layout_layout_visibilityStrategy, CONTENT)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: FrameLayout.LayoutParams) : super(source)
        constructor(source: LayoutParams) : super(source) {
            visibilityStrategy = source.visibilityStrategy
        }

        @IntDef(CONTENT, OTHER, ALWAYS)
        annotation class ShowStrategy

        companion object {
            const val MATCH_PARENT = -1
            const val WRAP_CONTENT = -2

            const val CONTENT = 0
            const val OTHER = 1
            const val OTHER_IGNORE_CONTENT = 2
            const val ALWAYS = 3
        }
    }

    class SavedState : AbsSavedState {

        var currentStateClassName: String? = null
        var preStateClassName: String? = null

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : this(source, null)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            currentStateClassName = source.readString()
            preStateClassName = source.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(currentStateClassName)
            dest.writeString(preStateClassName)
        }

        companion object CREATOR : Parcelable.ClassLoaderCreator<SavedState> {
            override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState {
                return SavedState(source, loader)
            }

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        fun bind(
            view: View,
            beginIsContent: Boolean = false,
            enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
        ): StateLayout {
            val stateLayout = StateLayout(view.context, null, 0, 0, beginIsContent, enableAnimation)
            if (view.parent == null) {
                stateLayout.addView(view, LayoutParams(view.layoutParams))
            } else {
                val parent = view.parent as ViewGroup
                val lp = view.layoutParams
                val index = parent.indexOfChild(view)
                parent.removeView(view)
                stateLayout.addView(view, LayoutParams(lp))
                parent.addView(stateLayout, index, lp)
            }
            return stateLayout
        }

        fun bind(
            activity: Activity,
            beginIsContent: Boolean = false,
            enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
        ): StateLayout {
            val contentView = activity.findViewById(android.R.id.content) as ViewGroup
            return bind(
                if (contentView.childCount > 0) contentView.getChildAt(0) else contentView,
                beginIsContent,
                enableAnimation
            )
        }

        fun bind(
            fragment: Fragment,
            beginIsContent: Boolean = false,
            enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
        ) = bind(fragment.requireView(), beginIsContent, enableAnimation)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled() = childHelper.isNestedScrollingEnabled

    override fun startNestedScroll(axes: Int, type: Int) =
        childHelper.startNestedScroll(axes, type)

    override fun stopNestedScroll(type: Int) =
        childHelper.stopNestedScroll(type)

    override fun hasNestedScrollingParent(type: Int) =
        childHelper.hasNestedScrollingParent(type)

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) = childHelper.dispatchNestedScroll(
        dxConsumed,
        dyConsumed,
        dxUnconsumed,
        dyUnconsumed,
        offsetInWindow,
        type,
        consumed
    )

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ) = childHelper.dispatchNestedScroll(
        dxConsumed,
        dyConsumed,
        dxUnconsumed,
        dyUnconsumed,
        offsetInWindow,
        type
    )

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ) = childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ) = childHelper.dispatchNestedFling(velocityX, velocityY, consumed)

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float) =
        childHelper.dispatchNestedPreFling(velocityX, velocityY)

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        childHelper.startNestedScroll(axes, type)
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
        childHelper.stopNestedScroll(type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            type,
            consumed
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            type
        )
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        childHelper.dispatchNestedPreScroll(dx, dy, consumed, null, type)
    }
}