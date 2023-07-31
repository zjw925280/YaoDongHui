package net.knowfx.yaodonghui.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.ViewConfiguration
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat


/**
 * @Description:
 * @Author: cbx
 * @CreateDate: 2021/2/20
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/2/20
 * @UpdateRemark: 更新说明
 */
object ViewUtil {
    /**使用上下文对象[context]获取状态栏高度*/
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId: Int = context.resources.getIdentifier(
            "status_bar_height", "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**通过Activity对象[act]隐藏软键盘*/
    fun hideKeyboard(act: Activity) {
        val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(act.window.decorView.windowToken, 0)
    }


    /**通过上下文对象[context]获取虚拟按键高度*/
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (hasNavBar(context)) {
            val res = context.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    /**使用上下文对象[context]获取是否有虚拟按键*/
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun hasNavBar(context: Context): Boolean {
        val res = context.resources
        val resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)
            // check override flag
            val sNavBarOverride = getNavBarOverride()
            if ("1" == sNavBarOverride) {
                hasNav = false
            } else if ("0" == sNavBarOverride) {
                hasNav = true
            }
            hasNav
        } else { // fallback
            !ViewConfiguration.get(context).hasPermanentMenuKey()
        }
    }

    /**判断虚拟按键是否被重写*/
    @SuppressLint("PrivateApi")
    private fun getNavBarOverride(): String {
        var sNavBarOverride = ""
        try {
            val c = Class.forName("android.os.SystemProperties")
            val m = c.getDeclaredMethod("get", String::class.java)
            m.isAccessible = true
            sNavBarOverride = m.invoke(null, "qemu.hw.mainkeys") as String
        } catch (e: Throwable) {
        }
        return sNavBarOverride
    }

    /**根据手机的分辨率从 px(像素)[pxValue] 的单位 转成为 dp*/
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**通过Activity[activity]修改状态栏颜色[colorId]*/
    fun setStatusBarColor(activity: Activity, colorId: Int) {
        val window: Window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, colorId)
    }
}

/**
 * 通过EditText展示隐藏键盘
 */
fun inputShowHide(editText: EditText, show: Boolean) {
    if (show) {
        val imm: InputMethodManager = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
    } else {
        val imm: InputMethodManager = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            editText.windowToken, 0
        )
    }
}