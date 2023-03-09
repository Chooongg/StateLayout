package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.state.AbstractState

class LottieLoadingState(context: Context) : AbstractState(context) {

    private val animationView = LottieAnimationView(context).apply {
        repeatCount = LottieDrawable.INFINITE
        setAnimation(R.raw.lottie_loading)
    }

    init {
        addView(animationView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationView.playAnimation()
    }

    override fun onChangeParams(params: Any?) {}

    override fun getRetryEventView(): View? = null
}