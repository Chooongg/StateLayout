package com.chooongg.widget.stateLayout

import com.chooongg.widget.stateLayout.state.AbstractState

inline fun <reified T : AbstractState> StateLayout.show(params: Any? = null) =
    show(T::class, params)