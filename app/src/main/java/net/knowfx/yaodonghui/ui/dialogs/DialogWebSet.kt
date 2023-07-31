package net.knowfx.yaodonghui.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogWebCopyBinding
import net.knowfx.yaodonghui.databinding.LayoutItemWebCopyBinding
import net.knowfx.yaodonghui.ext.copy
import net.knowfx.yaodonghui.ext.setOnclick

class DialogWebSet(private val webs: ArrayList<String>) : BaseBottomDialog() {
    private lateinit var mBinding: DialogWebCopyBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogWebCopyBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        repeat(webs.size) {
            val param = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            param.setMargins(0, 0, 0, resources.getDimensionPixelOffset(R.dimen.dp_10))
            val itemBinding = LayoutItemWebCopyBinding.inflate(layoutInflater)
            itemBinding.web.text = webs[it]
            itemBinding.copy.setOnclick { itemBinding.web.copy() }
            itemBinding.root.layoutParams = param
            mBinding.webContainer.addView(itemBinding.root)
        }
    }
}