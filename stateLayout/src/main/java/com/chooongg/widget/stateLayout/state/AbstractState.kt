package com.chooongg.widget.stateLayout.state

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.chooongg.widget.stateLayout.StateLayout

abstract class AbstractState constructor(context: Context) : FrameLayout(context) {

    open fun isShowAnimate(): Boolean = true

    open fun isHideAnimate(): Boolean = true

    open fun isMeanwhileContent(): Boolean = false

    open fun onChangeParams(params: Any?) {}

    open fun getRetryEventView(): View? = null

    open fun generateLayoutParams(): StateLayout.LayoutParams = StateLayout.LayoutParams(
        StateLayout.LayoutParams.WRAP_CONTENT,
        StateLayout.LayoutParams.WRAP_CONTENT,
        Gravity.CENTER
    )

    /**
     * 隐藏当前状态
     */
    protected fun hideCurrentState() {
        if (parent is StateLayout) {
            val stateLayout = parent as StateLayout
            stateLayout.post {
                stateLayout.hideOtherState(
                    this,
                    isHideAnimate() && stateLayout.isEnableAnimate && stateLayout.canUseAnimate()
                ) { stateLayout.showPrevious() }
            }
        }
    }
}