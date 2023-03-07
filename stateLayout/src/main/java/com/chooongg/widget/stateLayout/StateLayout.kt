package com.chooongg.widget.stateLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.view.children
import androidx.core.view.contains
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
    enableAnimation: Boolean = StateLayoutManager.enableAnimation
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 当前状态
     */
    var currentState: KClass<out AbstractState> = ContentState::class
        private set

    /**
     * 存在的非Content状态
     */
    private val existingOtherState = HashMap<KClass<out AbstractState>, AbstractState>()

    /**
     * 是否启用动画
     */
    var isEnableAnimation: Boolean = false

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
        a.recycle()
        if (!isInEditMode && !isContent && StateLayoutManager.beginState != ContentState::class) {
            show(StateLayoutManager.beginState)
        }
        isEnableAnimation = enableAnimation
    }

    /**
     * 显示内容
     */
    fun showContent() = show(ContentState::class)

//    fun <T : AbstractState> show(params: Any? = null) {
//
//    }

    fun show(stateClass: KClass<out AbstractState>, params: Any? = null) {
        if (stateClass == currentState) {
            existingOtherState[stateClass]?.also { it.onChangeParams(it.targetView, params) }
            return
        }
        post {
            hideAllOtherState()
            if (stateClass == ContentState::class) {
                showContentViews()
            } else {
                createOrShowState(stateClass, params)
            }
        }
    }

    private fun showContentViews() {
        children.forEach {
            val params = it.layoutParams
            if (params is LayoutParams && !params.isStateChildView) {
                when (params.showStrategy) {
                    LayoutParams.CONTENT_STATE -> {
                        if (canUseAnimation()) {
                            animate.showAnimate(it) { it.visibility = View.VISIBLE }
                        } else it.visibility = View.VISIBLE
                    }
                    LayoutParams.OTHER_STATE -> {
                        if (canUseAnimation()) {
                            animate.hideAnimate(it) { it.visibility = View.INVISIBLE }
                        } else it.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun hideContentViews() {
        children.forEach {
            val params = it.layoutParams
            if (params is LayoutParams && !params.isStateChildView) {
                when (params.showStrategy) {
                    LayoutParams.CONTENT_STATE -> {
                        if (canUseAnimation()) {
                            animate.showAnimate(it) { it.visibility = View.INVISIBLE }
                        } else it.visibility = View.INVISIBLE
                    }
                    LayoutParams.OTHER_STATE -> {
                        if (canUseAnimation()) {
                            animate.hideAnimate(it) { it.visibility = View.VISIBLE }
                        } else it.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun createOrShowState(stateClass: KClass<out AbstractState>, params: Any?) {
        val temp = existingOtherState[stateClass]
        val state = temp ?: stateClass.java.newInstance()
        if (temp == null) createState(state, params) else showState(state, params)
        if (state.isMeanwhileContent) showContentViews() else hideContentViews()
        existingOtherState[stateClass] = state
        currentState = stateClass
        onStatedChangeListener?.onStatedChange(stateClass)
    }

    private fun createState(state: AbstractState, params: Any?) {
        if (state.targetView.parent == null) addView(state.targetView)
        state.onAttach(this, params)
        if (canUseAnimation(state)) {
            animate.showAnimate(state.targetView) { state.targetView.visibility = View.VISIBLE }
        } else state.targetView.visibility = View.VISIBLE
    }

    private fun showState(state: AbstractState, params: Any?) {
        if (state.targetView.parent == null) addView(state.targetView)
        if (canUseAnimation(state)) {
            animate.showAnimate(state.targetView) { state.targetView.visibility = View.VISIBLE }
        } else state.targetView.visibility = View.VISIBLE
        state.onChangeParams(state.targetView, params)
    }

    private fun hideAllOtherState() {
        existingOtherState.forEach { hideState(it.key) }
    }

    private fun hideState(stateClass: KClass<out AbstractState>) {
        val state = existingOtherState[stateClass] ?: return
        if (canUseAnimation(state)) {
            animate.hideAnimate(state.targetView) {
                state.onDetach(state.targetView) {
                    if (contains(state.targetView)) removeView(state.targetView)
                    existingOtherState.remove(stateClass)
                }
            }
        } else {
            state.onDetach(state.targetView) {
                if (contains(state.targetView)) removeView(state.targetView)
                existingOtherState.remove(stateClass)
            }
        }
    }

    private fun canUseAnimation() =
        isEnableAnimation && isAttachedToWindow && isVisible && isEnabled

    private fun canUseAnimation(state: AbstractState) =
        state.isEnableHideAnimation && isEnableAnimation && isAttachedToWindow && isVisible && isEnabled

    /**
     * 设置重试操作监听
     */
    fun setOnRetryEventListener(block: (() -> Unit)) {
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

    /** @hide */
    protected open fun onSetLayoutParams(child: View?, layoutParams: ViewGroup.LayoutParams?) {
        checkChildViewUpdateIsNeedRequestLayout(child, layoutParams)
        requestLayout()
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
        val lp = params ?: generateDefaultLayoutParams()
        checkChildViewUpdateIsNeedRequestLayout(child, lp)
        super.addView(child, index, lp)
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
            layoutParams.isStateChildView -> return
            layoutParams.showStrategy == LayoutParams.CONTENT_STATE -> {
                if (currentState == ContentState::class) {
                    if (child.visibility != View.VISIBLE) {
                        child.visibility = View.VISIBLE
                    }
                } else {
                    if (child.visibility == View.VISIBLE) {
                        child.visibility = View.INVISIBLE
                    }
                }
            }
            layoutParams.showStrategy == LayoutParams.OTHER_STATE -> {
                if (currentState == ContentState::class) {
                    if (child.visibility == View.VISIBLE) {
                        child.visibility = View.INVISIBLE
                    }
                } else {
                    if (child.visibility != View.VISIBLE) {
                        child.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    class LayoutParams : FrameLayout.LayoutParams {

        internal var isStateChildView: Boolean = false

        @ShowStrategy
        var showStrategy: Int = CONTENT_STATE

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.StateLayout_Layout)
            showStrategy =
                a.getInt(R.styleable.StateLayout_Layout_layout_showStrategy, CONTENT_STATE)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: FrameLayout.LayoutParams) : super(source)
        constructor(source: LayoutParams) : super(source) {
            showStrategy = source.showStrategy
        }

        @IntDef(CONTENT_STATE, OTHER_STATE, ALWAYS)
        annotation class ShowStrategy

        companion object {
            const val MATCH_PARENT = -1
            const val WRAP_CONTENT = -2

            const val CONTENT_STATE = 0
            const val OTHER_STATE = 1
            const val ALWAYS = 2
        }
    }
}