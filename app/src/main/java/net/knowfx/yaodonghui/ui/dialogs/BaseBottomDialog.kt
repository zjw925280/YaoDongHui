package net.knowfx.yaodonghui.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

open class BaseBottomDialog : DialogFragment() {
    override fun onStart() {
        super.onStart()

        // 设置宽度为屏宽, 靠近屏幕底部。
        val win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //全屏化对话框
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = resources.displayMetrics.widthPixels
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }
}