package com.chooongg.widget.stateLayout.state

import android.content.Context
import android.view.Gravity
import android.view.View
import com.chooongg.widget.stateLayout.StateLayout

/**
 * 状态抽象类 实现类必须有无参构造方法
 */
abstract class AbstractState {

    /**
     * 是否同时显示内容
     */
    open val isMeanwhileContent: Boolean = false

    /**
     * 是否开启显示动画
     */
    abstract val isEnableShowAnimation: Boolean

    /**
     * 是否开启隐藏动画
     */
    abstract val isEnableHideAnimation: Boolean

    internal lateinit var targetView: View

    internal fun obtainTargetView(context: Context) {
        targetView = onCreateView(context)
    }

    /**
     * 生成状态View
     */
    abstract fun onCreateView(context: Context): View

    /**
     * 连接时
     */
    abstract fun onAttach(view: View, params: Any?)

    /**
     * 修改参数时
     */
    abstract fun onChangeParams(view: View, params: Any?)

    /**
     * 断开时
     */
    open fun onDetach(view: View, removeBlock: () -> Unit) {
        removeBlock.invoke()
    }

    /**
     * 获取重新加载事件View
     */
    open fun getReloadEventView(parent: View, view: View): View? = parent

    open fun generateLayoutParams(): StateLayout.LayoutParams {
        return StateLayout.LayoutParams(
            StateLayout.LayoutParams.WRAP_CONTENT,
            StateLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
    }
}