package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.state.AbstractState
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressState : AbstractState() {
    override val isEnableShowAnimation: Boolean = true
    override val isEnableHideAnimation: Boolean = true
    override fun onCreateView(context: Context): View {
        return CircularProgressIndicator(context).apply {
            trackCornerRadius = 500
        }
    }

    override fun onAttach(view: View, params: Any?) {
        val indicator = view as CircularProgressIndicator
        if (params is Number) {
            indicator.isIndeterminate = false
            indicator.setProgress(params.toInt(),true)
        } else {
            indicator.isIndeterminate = true
        }
    }

    override fun onChangeParams(view: View, params: Any?) {
        onAttach(view, params)
    }

    override fun getReloadEventView(parent: View, view: View) = null
}