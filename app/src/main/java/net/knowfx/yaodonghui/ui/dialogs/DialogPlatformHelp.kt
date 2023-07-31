package net.knowfx.yaodonghui.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogPlatformHelpBinding
import net.knowfx.yaodonghui.entities.BrokerContentData
import net.knowfx.yaodonghui.entities.HelpData
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity

class DialogPlatformHelp(
    private val data: HelpData,
    private val onSubmitClick: ((data: BrokerContentData?) -> Unit)?
) :
    BaseBottomDialog() {
    private lateinit var mBinding: DialogPlatformHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogPlatformHelpBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.logoHelp.intoCorners(data.logofile, resources.getDimension(R.dimen.dp_5))
        val spannable = SpannableString(data.unit + data.amount)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.color_E76E32)),
            data.unit.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.costMoney.text = spannable
        mBinding.costTime.text = data.assureDays
        mBinding.textWarn1.text = data.describe1
        mBinding.textWarn2.text = data.describe2
        mBinding.btnUrl.setOnclick {
            requireActivity().startActivity(
                Intent(
                    requireContext(),
                    BrokerContentActivity::class.java
                )
            )
        }

        mBinding.btnConfirm.setOnclick {
            //提交申请
            onSubmitClick?.invoke(null)
        }
    }
}