package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import com.chooongg.widget.stateLayout.state.AbstractState
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressState : AbstractState() {
    override val isEnableShowAnimation: Boolean = false
    override val isEnableHideAnimation: Boolean = false
    override fun onCreateView(context: Context): View {
        return CircularProgressIndicator(context).apply {
            visibility = View.GONE
            isIndeterminate = true
            showAnimationBehavior = BaseProgressIndicator.SHOW_INWARD
            hideAnimationBehavior = BaseProgressIndicator.HIDE_INWARD
        }
    }

    override fun onAttach(view: View, params: Any?) {
        val indicator = view as CircularProgressIndicator
        if (params is Number) {
            indicator.isIndeterminate = false
            indicator.progress = params.toInt()
        } else {
            indicator.isIndeterminate = true
        }
    }

    override fun onChangeParams(view: View, params: Any?) {
        onAttach(view, params)
    }

    override fun onDetach(view: View, removeBlock: () -> Unit) {
    }

    override fun getReloadEventView(parent: View, view: View) = null
}