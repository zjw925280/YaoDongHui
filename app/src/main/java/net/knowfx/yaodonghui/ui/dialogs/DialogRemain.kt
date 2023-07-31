package net.knowfx.yaodonghui.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogTwoButtonsBinding
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.trueLet

@SuppressLint("ViewConstructor")
class DialogRemain : CenterPopupView {

    private val mTitle: String
    private val mContent: String
    private val mCancelStr: String
    private val mConfirmStr: String
    private val cancelListener: (() -> Unit)?
    private val confirmListener: () -> Unit

    constructor(
        context: Context,
        content: String,
        title: String = "",
        onConfirm: () -> Unit
    ) : super(context) {
        mTitle = title
        mContent = content
        mCancelStr = ""
        mConfirmStr = ""
        cancelListener = null
        confirmListener = onConfirm
    }

    constructor(
        context: Context,
        content: String,
        title: String = "",
        cancelStr: String = "",
        confirmStr: String = "",
        onCancel: (() -> Unit)? = null,
        onConfirm: (() -> Unit)
    ) : super(context) {
        mTitle = title
        mContent = content
        mCancelStr = cancelStr
        mConfirmStr = confirmStr
        cancelListener = onCancel
        confirmListener = onConfirm
    }

    override fun getImplLayoutId() = R.layout.dialog_two_buttons

    override fun onCreate() {
        super.onCreate()
        val viewBinding = DialogTwoButtonsBinding.bind(centerPopupContainer.getChildAt(0))
        viewBinding.title.apply {
            if (mTitle.isNotEmpty()) {
                text = mTitle
            } else {
                gone()
            }
        }

        viewBinding.content.text = mContent

        mCancelStr.isNotEmpty().trueLet {
            viewBinding.btnLeft.text = mCancelStr
        }

        mConfirmStr.isNotEmpty().trueLet {
            viewBinding.btnRight.text = mConfirmStr
        }
        setMultipleClick(viewBinding.btnLeft, viewBinding.btnRight) {
            when (it) {
                viewBinding.btnLeft -> cancelListener?.invoke()
                viewBinding.btnRight -> confirmListener.invoke()
                else -> {}
            }
            dismiss()
        }
    }
}