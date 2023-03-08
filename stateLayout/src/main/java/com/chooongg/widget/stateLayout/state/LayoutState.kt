package com.chooongg.widget.stateLayout.state

import android.view.Gravity
import android.view.View
import com.chooongg.widget.stateLayout.StateLayout

interface LayoutState {

    fun isShowAnimate(): Boolean = true

    fun isHideAnimate(): Boolean = true

    fun isMeanwhileContent(): Boolean = false

    fun getRetryEventView(): View? = null

    fun generateLayoutParams(): StateLayout.LayoutParams = StateLayout.LayoutParams(
        StateLayout.LayoutParams.WRAP_CONTENT,
        StateLayout.LayoutParams.WRAP_CONTENT,
        Gravity.CENTER
    )
}