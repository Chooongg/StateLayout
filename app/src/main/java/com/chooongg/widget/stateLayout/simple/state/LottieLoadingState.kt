package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.airbnb.lottie.LottieDrawable
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.simple.databinding.StateLottieBinding
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.math.min

class LottieLoadingState(context: Context) : AbstractState(context) {

    val binding = StateLottieBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
        binding.lottieView.repeatCount = LottieDrawable.INFINITE
        binding.lottieView.setAnimation(R.raw.lottie_loading)
        binding.tvMessage.visibility = View.GONE
        post {
            val minSize = min(measuredWidth, measuredHeight)
            binding.lottieView.updateLayoutParams<LinearLayout.LayoutParams> {
                width = minSize / 2
                height = minSize / 2
            }
        }
    }

    override fun generateLayoutParams() = StateLayout.LayoutParams(
        StateLayout.LayoutParams.MATCH_PARENT,
        StateLayout.LayoutParams.MATCH_PARENT
    )

    override fun getRetryEventView() = null
}