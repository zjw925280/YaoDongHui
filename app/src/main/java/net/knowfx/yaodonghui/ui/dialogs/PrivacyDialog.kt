package net.knowfx.yaodonghui.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogPriConfirmBinding
import net.knowfx.yaodonghui.entities.ContractData
import net.knowfx.yaodonghui.ext.setMultipleClick
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

@SuppressLint("ViewConstructor")
class PrivacyDialog(
    context: Context,
    private val data: ContractData,
    private val callback: (isAccess: Boolean) -> Unit
) : CenterPopupView(context) {
    override fun getImplLayoutId() = R.layout.dialog_pri_confirm

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate() {
        super.onCreate()
        val binding = DialogPriConfirmBinding.bind(centerPopupContainer.getChildAt(0))
        binding.title.text = data.labelName
        binding.content.setHtml(data.labelValue, HtmlHttpImageGetter(binding.content))
        setMultipleClick(binding.btnCancel, binding.btnConfirm){
            callback.invoke(it == binding.btnConfirm)
            dismiss()
        }
    }
}