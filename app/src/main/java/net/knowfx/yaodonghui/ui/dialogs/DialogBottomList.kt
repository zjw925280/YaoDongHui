package net.knowfx.yaodonghui.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.DialogBottomListBinding
import net.knowfx.yaodonghui.entities.BottomPopData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.visible

class DialogBottomList(
    private val title: String = "",
    private val bottomText: String = "",
    onClick: (position: Int) -> Unit
) :
    BaseBottomDialog() {
    private lateinit var mBinding: DialogBottomListBinding
    private val mDataList = ArrayList<BottomPopData>()
    private val mAdapter = CommonListAdapter<BottomPopData> { _, _, position ->
        onClick.invoke(position)
        dismissAllowingStateLoss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogBottomListBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.dialogBottomTitle.apply {
            if (title.isNotEmpty()) {
                visible()
                text = title
            } else {
                gone()
            }
        }
        mBinding.dialogBottomRv.layoutManager = LinearLayoutManager(context)
        mBinding.dialogBottomRv.adapter = mAdapter
        mBinding.btnBottom.apply {
            if (bottomText.isNotEmpty()) {
                text = bottomText
                setOnclick {
                    dismiss()
                }
            } else {
                gone()
            }
        }
    }

    fun setDataList(list: ArrayList<BottomPopData>): DialogBottomList {
        mDataList.clear()
        mDataList.addAll(list)
        return this
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        mAdapter.putData(mDataList)
    }
}