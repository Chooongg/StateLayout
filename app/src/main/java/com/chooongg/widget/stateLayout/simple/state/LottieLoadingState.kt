package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.math.min

class LottieLoadingState(context: Context) : AbstractState(context) {

    private val animationView = LottieAnimationView(context).apply {
        repeatCount = LottieDrawable.INFINITE
        setAnimation(R.raw.lottie_loading)
    }

    init {
        addView(animationView)
        viewTreeObserver.addOnGlobalLayoutListener(object:OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                animationView.updateLayoutParams<LayoutParams> {
                    setMargins(min(width, height) / 2)
                }
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationView.playAnimation()
    }

    override fun onChangeParams(params: Any?) {}

    override fun getRetryEventView(): View? = null
}