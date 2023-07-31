package net.knowfx.yaodonghui.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogCommonInputBinding
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast

@SuppressLint("ViewConstructor")
class DialogInput(
    context: Context,
    private val title: String = "",
    private val hint: String = "",
    private val callback: (str: String) -> Unit
) : CenterPopupView(context) {
    override fun getImplLayoutId() = R.layout.dialog_common_input

    override fun onCreate() {
        super.onCreate()
        val binding = DialogCommonInputBinding.bind(centerPopupContainer.getChildAt(0))
        binding.dialogTitle.apply {
            if (title.isNotEmpty()) {
                text = title
            } else {
                gone()
            }
        }
        binding.dialogInput.hint = hint
        binding.btnConfirm.setOnclick {
            val str = binding.dialogInput.text.toString().trim()
            if (str.isNotEmpty()) {
                callback.invoke(str)
                dismiss()
            } else {
                "请输入新昵称".toast()
            }
        }
    }
}