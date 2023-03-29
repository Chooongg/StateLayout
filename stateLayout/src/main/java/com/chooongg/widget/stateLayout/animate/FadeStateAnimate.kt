package com.chooongg.widget.stateLayout.animate

import android.view.View

class FadeStateAnimate : StateAnimate() {

    override fun createAnimate(view: View) {
        view.alpha = 0f
    }

    override fun showAnimate(view: View, startBlock: () -> Unit, endBlock: () -> Unit) {
        view.animate().cancel()
        view.animate().alpha(1f).withStartAction(startBlock).withEndAction(endBlock)
    }

    override fun hideAnimate(view: View, startBlock: () -> Unit, endBlock: () -> Unit) {
        view.animate().cancel()
        view.animate().alpha(0f).withStartAction(startBlock).withEndAction(endBlock)
    }

    override fun resetAnimate(view: View) {
        view.alpha = 1f
    }
}