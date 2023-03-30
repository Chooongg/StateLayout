package com.chooongg.widget.stateLayout.simple.state

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.state.AbstractState

class LottieCelebrationState(context: Context) : AbstractState(context) {

    override fun isShowAnimate(): Boolean = false
    override fun isHideAnimate(): Boolean = true
    override fun isMeanwhileContent(): Boolean = true

    private val animationView = LottieAnimationView(context).apply {
        scaleType = ImageView.ScaleType.CENTER
        setAnimation(R.raw.lottie_celebration)
    }

    init {
        addView(ConstraintLayout(context).apply {
            addView(animationView, ConstraintLayout.LayoutParams(0, 0).apply {
                startToStart = 0
                endToEnd = 0
                topToTop = 0
                bottomToBottom = 0
            })
        })
        animationView.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // 调用父类的隐藏当前状态方法
                hideCurrentState()
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationView.playAnimation()
    }

    override fun onChangeParam(params: Any?) {}

    override fun getRetryEventView(): View? = null

    override fun generateLayoutParams(): StateLayout.LayoutParams {
        return StateLayout.LayoutParams(-1, -1)
    }
}