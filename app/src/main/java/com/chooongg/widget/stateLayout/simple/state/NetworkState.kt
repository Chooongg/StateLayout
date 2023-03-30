package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.simple.R
import com.chooongg.widget.stateLayout.simple.databinding.StateLottieBinding
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.math.min

class NetworkState(context: Context) : AbstractState(context) {

    val binding = StateLottieBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
        binding.lottieView.setAnimation(R.raw.lottie_network)
        post {
            val minSize = min(measuredWidth, measuredHeight)
            binding.lottieView.updateLayoutParams<LinearLayout.LayoutParams> {
                width = minSize / 2
                height = minSize / 2
            }
        }
    }

    override fun onChangeParam(params: Any?) {
        binding.tvMessage.text = params?.toString() ?: context.getString(R.string.message_network)
    }

    override fun generateLayoutParams() = StateLayout.LayoutParams(
        StateLayout.LayoutParams.MATCH_PARENT,
        StateLayout.LayoutParams.MATCH_PARENT
    )
}