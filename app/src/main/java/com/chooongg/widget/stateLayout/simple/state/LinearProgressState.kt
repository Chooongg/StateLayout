package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.Gravity
import android.view.View
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.state.AbstractState
import com.google.android.material.progressindicator.LinearProgressIndicator

class LinearProgressState(context: Context) : AbstractState(context) {

    val indicator = LinearProgressIndicator(context)

    init {
        addView(indicator)
    }

    override fun isMeanwhileContent(): Boolean = true

    override fun onChangeParam(params: Any?) {
        if (params is Number) {
            indicator.isIndeterminate = false
            indicator.setProgress(params.toInt(), true)
        } else {
            indicator.isIndeterminate = true
        }
    }

    override fun generateLayoutParams(): StateLayout.LayoutParams {
        return StateLayout.LayoutParams(
            StateLayout.LayoutParams.MATCH_PARENT,
            StateLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        )
    }

    override fun getRetryEventView(): View? = null
}