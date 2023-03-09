package com.chooongg.widget.stateLayout

import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.reflect.KClass

/**
 * 重试事件监听
 */
fun interface OnRetryEventListener {
    /**
     * 重试事件
     */
    fun onStateRetry(state: KClass<out AbstractState>)
}