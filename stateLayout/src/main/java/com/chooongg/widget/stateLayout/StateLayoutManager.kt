package com.chooongg.widget.stateLayout

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.forEachIndexed
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

    /**
     * 附加状态布局到Activity
     */
    fun attachStateLayout(
        activity: Activity,
        beginIsContent: Boolean = false,
        enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
    ): StateLayout {
        val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
        val stateLayout = StateLayout(activity, null, 0, 0, beginIsContent, enableAnimation)
        if (contentView.childCount > 0) {
            val childView = contentView.getChildAt(0)
            val lp = childView.layoutParams
            contentView.removeView(childView)
            stateLayout.addView(
                childView, StateLayout.LayoutParams(
                    StateLayout.LayoutParams.MATCH_PARENT,
                    StateLayout.LayoutParams.MATCH_PARENT
                )
            )
            contentView.addView(stateLayout, lp)
        } else contentView.addView(
            stateLayout, StateLayout.LayoutParams(
                StateLayout.LayoutParams.MATCH_PARENT,
                StateLayout.LayoutParams.MATCH_PARENT
            )
        )
        return stateLayout
    }

    /**
     * 附加状态布局到View
     */
    fun attachStateLayout(
        view: View,
        beginIsContent: Boolean = false,
        enableAnimation: Boolean = StateLayoutManager.isEnableAnimation
    ): StateLayout {
        val stateLayout = StateLayout(view.context, null, 0, 0, beginIsContent, enableAnimation)
        if (view.parent != null) {
            val parent = view.parent as ViewGroup
            var childIndex = 0
            parent.forEachIndexed { index, it ->
                if (it == view) {
                    childIndex = index
                    return@forEachIndexed
                }
            }
            val lp = view.layoutParams
            parent.removeView(view)
            stateLayout.addView(
                view, StateLayout.LayoutParams(
                    StateLayout.LayoutParams.MATCH_PARENT,
                    StateLayout.LayoutParams.MATCH_PARENT
                )
            )
            parent.addView(stateLayout, childIndex, lp)
        } else stateLayout.addView(
            view, StateLayout.LayoutParams(
                StateLayout.LayoutParams.MATCH_PARENT,
                StateLayout.LayoutParams.MATCH_PARENT
            )
        )
        return stateLayout
    }
}