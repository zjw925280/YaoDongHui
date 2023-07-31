package net.knowfx.yaodonghui.utils

import com.orhanobut.logger.Logger
import net.knowfx.yaodonghui.ext.trueLet

/**
 * @Description: 日志输出类
 * @Author: cbx
 * @CreateDate: 2021/1/23
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/2
 * @UpdateRemark: 删除部分不使用的日志方法，去掉Debug判断
 */
object LogUtils {
    fun e(msg: String) {
        Logger.e(msg)
    }

    fun i(msg: String) {
        Logger.i(msg)
    }

    fun d(msg: String) {
        Logger.d(msg)
    }

    fun json(jsonStr: String) {
        Logger.json(jsonStr)
    }

    fun e(vararg args: String) {
        val msg = StringBuilder()
        (args.isNotEmpty()).trueLet {
            msg.append("|$args")
        }
        Logger.e(msg.toString())
    }
}