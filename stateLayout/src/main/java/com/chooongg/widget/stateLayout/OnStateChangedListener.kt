package com.chooongg.widget.stateLayout

import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.reflect.KClass

/**
 * 状态改变监听
 */
fun interface OnStateChangedListener {
    /**
     * 状态改变
     */
    fun onStateChanged(state: KClass<out AbstractState>)
}