package com.chooongg.widget.stateLayout.simple.state

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.state.AbstractState

class LottieCelebrationState(context: Context) : AbstractState(context) {

    override fun isShowAnimate(): Boolean = false
    override fun isHideAnimate(): Boolean = false
    override fun isMeanwhileContent(): Boolean = true

    private val animationView = LottieAnimationView(context).apply {
        repeatCount = 1
        scaleType = ImageView.ScaleType.CENTER_CROP
        setAnimation(R.raw.lottie_celebration)
    }

    init {
        addView(animationView)
        animationView.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                hideCurrentState(false)
                Toast.makeText(context, "动画结束", Toast.LENGTH_SHORT).show()
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