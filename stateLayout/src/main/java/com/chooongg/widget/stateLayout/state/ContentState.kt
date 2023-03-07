package com.chooongg.widget.stateLayout.state

import android.content.Context
import android.view.View

class ContentState : AbstractState() {
    override val isEnableShowAnimation: Boolean = true
    override val isEnableHideAnimation: Boolean = true

    override fun onCreateView(context: Context) = View(context)
    override fun onAttach(view: View, params: Any?) {}
    override fun onChangeParams(view: View, params: Any?) {}
    override fun getReloadEventView(parent: View, view: View) = null
}