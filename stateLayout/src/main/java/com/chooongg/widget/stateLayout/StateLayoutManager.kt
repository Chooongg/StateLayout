package com.chooongg.widget.stateLayout

import com.chooongg.widget.stateLayout.animate.FadeStateAnimate
import com.chooongg.widget.stateLayout.animate.StateAnimate
import com.chooongg.widget.stateLayout.state.AbstractState
import com.chooongg.widget.stateLayout.state.ContentState
import kotlin.reflect.KClass

/**
 * 状态布局全局管理器
 */
object StateLayoutManager {

    /**
     * 开始状态
     */
    var beginState: KClass<out AbstractState> = ContentState::class

    /**
     * 是否开启动画
     */
    var isEnableAnimation: Boolean = true

    /**
     * 动画
     */
    var animate: StateAnimate = FadeStateAnimate()
}