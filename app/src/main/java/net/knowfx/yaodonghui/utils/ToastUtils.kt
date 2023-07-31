package net.knowfx.yaodonghui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.widget.Toast

/**
 * Toast 工具类
 */
class ToastUtils {
    companion object {
        private var lastToast: Toast? = null

        @SuppressLint("ShowToast")
        fun showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
            if (TextUtils.isEmpty(message)) return

            // 9.0 以上直接用调用即可防止重复的显示的问题，且如果复用 Toast 会出现无法再出弹出对话框问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Toast.makeText(getContext(), message, duration).show()
            } else {
                if (lastToast != null) {
                    lastToast!!.setText(message)
                } else {
                    lastToast = Toast.makeText(getContext(), message, duration)
                }
                lastToast!!.show()
            }
        }

        private fun getContext(): Context {
            return if (MyApplication.getLastActivity() != null) {
                MyApplication.getLastActivity()!!
            } else {
                MyApplication.getInstance()
            }
        }
    }
}