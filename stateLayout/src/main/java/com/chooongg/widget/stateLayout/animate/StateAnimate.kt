package com.chooongg.widget.stateLayout.animate

import android.view.View

abstract class StateAnimate {
    abstract fun createAnimate(view: View)
    abstract fun showAnimate(view: View, startBlock: () -> Unit, endBlock: () -> Unit)
    abstract fun hideAnimate(view: View, startBlock: () -> Unit, endBlock: () -> Unit)

    fun showAnimate(view: View, startBlock: () -> Unit) {
        showAnimate(view, startBlock) {}
    }

    fun hideAnimate(view: View, endBlock: () -> Unit) {
        hideAnimate(view, {}, endBlock)
    }
}