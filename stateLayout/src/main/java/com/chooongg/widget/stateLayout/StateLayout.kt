package com.chooongg.widget.stateLayout

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.AbsSavedState
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.chooongg.widget.stateLayout.animate.StateAnimate
import com.chooongg.widget.stateLayout.state.AbstractState
import com.chooongg.widget.stateLayout.state.ContentState
import kotlin.reflect.KClass

open class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    beginIsContent: Boolean = false,
    enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 当前状态
     */
    var currentState: KClass<out AbstractState> = ContentState::class
        private set

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
    private var onStatedChangeListener: OnStatedChangeListener? = null

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.StateLayout, defStyleAttr, defStyleRes
        )
        val isContent = a.getBoolean(R.styleable.StateLayout_beginIsContent, beginIsContent)
        if (!isInEditMode && !isContent && StateLayoutManager.beginState != ContentState::class) {
            showInternal(StateLayoutManager.beginState, null, false)
        }
        a.recycle()
    }

    /**
     * show content state
     */
    fun showContent() = show(ContentState::class)

    /**
     * show state
     */
    fun show(stateClass: KClass<out AbstractState>, params: Any? = null) {
        post { showInternal(stateClass, params, isEnableAnimate) }
    }

    /**
     * show status internal implementation
     */
    private fun showInternal(
        stateClass: KClass<out AbstractState>, params: Any? = null, isAnimate: Boolean
    ) {
        if (stateClass == currentState) {
            forEach { if (it::class == stateClass) (it as AbstractState).onChangeParams(params) }
            return
        }
        val isHasState = children.find { it::class == stateClass }
        if (isHasState == null && stateClass != ContentState::class) createOtherState(stateClass)
        forEach {
            if (stateClass == ContentState::class) {
                showContentState(it, isAnimate)
                hideOtherState(it, isAnimate)
            } else {
                showOtherState(it, stateClass, params, isAnimate)
            }
        }
        currentState = stateClass
        onStatedChangeListener?.onStatedChange(stateClass)
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
            } else view.visibility = View.VISIBLE
            // 如果是其他状态显示策略，那么就隐藏
            LayoutParams.OTHER -> if (isAnimate && canUseAnimate()) {
                animate.hideAnimate(view) { view.visibility = View.GONE }
            } else view.visibility = View.GONE
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
            } else view.visibility = View.VISIBLE
        }
    }

    private fun createOtherState(stateClass: KClass<out AbstractState>) {
        val constructor = stateClass.java.getConstructor(Context::class.java)
        val state = constructor.newInstance(context)
        addView(state, state.generateLayoutParams())
        state.getRetryEventView()?.setOnClickListener {
            if (isEnabled) onRetryEventListener?.onStateRetry(stateClass)
        }
    }

    private fun showOtherState(
        view: View, stateClass: KClass<out AbstractState>, params: Any?, isAnimate: Boolean
    ) {
        if (view::class == stateClass) {
            val stateView = (view as AbstractState)
            stateView.onChangeParams(params)
            if (isAnimate && stateView.isShowAnimate() && canUseAnimate()) {
                if (stateView.visibility != View.VISIBLE) animate.createAnimate(stateView)
                animate.showAnimate(stateView) { stateView.visibility = View.VISIBLE }
            } else stateView.visibility = View.VISIBLE
            forEach {
                if (stateView.isMeanwhileContent()) showContentState(it, isAnimate)
                else hideContentState(it, isAnimate)
            }
        } else hideOtherState(view, isAnimate)
    }

    internal fun hideOtherState(view: View, isAnimate: Boolean) {
        val lp = view.layoutParams
        if (lp !is LayoutParams) return
        if (!lp.isStateView) return
        val stateView = (view as AbstractState)
        if (isAnimate && stateView.isHideAnimate() && canUseAnimate()) {
            animate.hideAnimate(stateView) { removeView(stateView) }
        } else removeView(stateView)
    }


    private fun canUseAnimate() = isAttachedToWindow && isVisible && isEnabled

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
    fun setOnStatedChangeListener(block: ((currentState: KClass<out AbstractState>) -> Unit)) {
        onStatedChangeListener = OnStatedChangeListener(block)
    }

    /**
     * 设置状态变化监听
     */
    fun setOnStatedChangeListener(listener: OnStatedChangeListener?) {
        onStatedChangeListener = listener
    }

    protected open fun onSetLayoutParams(child: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkChildViewUpdateIsNeedRequestLayout(child, layoutParams)
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState(super.onSaveInstanceState())
        state.currentStateClassName = currentState.java.name
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
            val temp = onStatedChangeListener
            onStatedChangeListener = null
            showInternal(
                Class.forName(state.currentStateClassName!!).kotlin as KClass<out AbstractState>,
                null,
                false
            )
            onStatedChangeListener = temp
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
        if (child == null) return
        if (layoutParams !is LayoutParams) return
        when {
            layoutParams.isStateView -> child.visibility =
                if (currentState == child::class) View.VISIBLE else View.GONE
            layoutParams.visibilityStrategy == LayoutParams.CONTENT -> child.visibility =
                if (currentState == ContentState::class) View.VISIBLE else View.GONE
            layoutParams.visibilityStrategy == LayoutParams.OTHER -> child.visibility =
                if (currentState == ContentState::class) View.INVISIBLE else View.GONE
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
            const val ALWAYS = 2
        }
    }

    class SavedState : AbsSavedState {

        var currentStateClassName: String? = null

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : this(source, null)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            currentStateClassName = source.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(currentStateClassName)
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
}