package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.state.AbstractState

class LottieLoadingState : AbstractState() {
    override val isEnableShowAnimation: Boolean = true
    override val isEnableHideAnimation: Boolean = true
    override fun onCreateView(context: Context): View {
        return LottieAnimationView(context).apply {
            repeatCount = LottieDrawable.INFINITE
            setAnimation(R.raw.lottie_loading)
            playAnimation()
        }
    }

    override fun onAttach(view: View, params: Any?) {
        val animationView = view as LottieAnimationView
        animationView.playAnimation()
    }

    override fun onChangeParams(view: View, params: Any?) {
    }

    override fun onDetach(view: View, removeBlock: () -> Unit) {
        val animationView = view as LottieAnimationView
        animationView.cancelAnimation()
        removeBlock.invoke()
    }

    override fun getReloadEventView(parent: View, view: View) = null
}