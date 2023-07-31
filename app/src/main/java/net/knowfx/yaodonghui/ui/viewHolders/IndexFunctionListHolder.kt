package net.knowfx.yaodonghui.ui.viewHolders

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.LayoutInnerRvBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.IndexFunctionListData
import net.knowfx.yaodonghui.ext.indexFunctionJump
import net.knowfx.yaodonghui.ui.activity.SuperviseContentActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration

class IndexFunctionListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    private val mAdapter =
        CommonListAdapter<IndexFunctionListData.IndexFunctionData> { _, data, _ ->
            data as IndexFunctionListData.IndexFunctionData
            (parent.context as BaseActivity).indexFunctionJump(data)
        }

    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        setIsRecyclable(false)
        data as IndexFunctionListData
        val binding = LayoutInnerRvBinding.bind(itemView)
        binding.root.setHasFixedSize(true)
        binding.root.isNestedScrollingEnabled = false
        val offsetV = itemView.resources.getDimensionPixelOffset(R.dimen.dp_18)
        binding.root.addItemDecoration(CommonMarginDecoration(offsetV, 0, 5, false))
        binding.root.layoutManager = object : GridLayoutManager(itemView.context, 5) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.root.adapter = mAdapter
        mAdapter.putData(dataList = data.indexFunctionList)
    }

}