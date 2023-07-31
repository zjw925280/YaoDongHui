package net.knowfx.yaodonghui.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import net.knowfx.yaodonghui.databinding.LayoutBtnTextBinding
import net.knowfx.yaodonghui.ext.into

class BtnWithRightTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var mBinding: LayoutBtnTextBinding
    override fun onFinishInflate() {
        mBinding = LayoutBtnTextBinding.inflate(LayoutInflater.from(context))
        addView(mBinding.root)
        super.onFinishInflate()
    }

    fun setIconAndText(iconId: Int, text: String) {
        mBinding.btnIv.into(iconId)
        mBinding.btnTv.text = text
    }

    fun updateText(text: String){
        mBinding.btnTv.text = text
    }
}