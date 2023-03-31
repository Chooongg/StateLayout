package com.chooongg.widget.stateLayout.simple.state

import android.content.Context
import android.view.View
import android.widget.TextView
import com.chooongg.widget.stateLayout.state.AbstractState

class TextState(context: Context) : AbstractState(context) {

    val textView = TextView(context)

    init {
        addView(textView)
    }

    override fun isMeanwhileContent() = false

    override fun onChangeParam(params: Any?) {
        textView.text = params?.toString() ?: "默认文本"
    }

    override fun getRetryEventView() = parent as? View
}