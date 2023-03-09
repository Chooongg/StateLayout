package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import com.chooongg.widget.stateLayout.state.AbstractState
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressState(context: Context) : AbstractState(context) {

    val indicator = CircularProgressIndicator(context).apply {
        trackCornerRadius = 500
    }

    init {
        addView(indicator)
    }

    override fun onChangeParams(params: Any?) {
        if (params is Number) {
            indicator.isIndeterminate = false
            indicator.setProgress(params.toInt(), true)
        } else {
            indicator.isIndeterminate = true
        }
    }

    override fun getRetryEventView(): View? = null
}