package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.Gravity
import android.view.View
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.state.AbstractState
import com.google.android.material.progressindicator.LinearProgressIndicator

class LinearProgressState : AbstractState() {
    override val isEnableShowAnimation: Boolean = true
    override val isEnableHideAnimation: Boolean = true
    override val isMeanwhileContent: Boolean = true
    override fun onCreateView(context: Context): View {
        return LinearProgressIndicator(context)
    }

    override fun onAttach(view: View, params: Any?) {
        val indicator = view as LinearProgressIndicator
        if (params is Number) {
            indicator.isIndeterminate = false
            indicator.setProgress(params.toInt(), true)
        } else {
            indicator.isIndeterminate = true
        }
    }

    override fun onChangeParams(view: View, params: Any?) {
        onAttach(view, params)
    }

    override fun generateLayoutParams(): StateLayout.LayoutParams {
        return StateLayout.LayoutParams(
            StateLayout.LayoutParams.MATCH_PARENT,
            StateLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        )
    }

    override fun getReloadEventView(parent: View, view: View) = null
}