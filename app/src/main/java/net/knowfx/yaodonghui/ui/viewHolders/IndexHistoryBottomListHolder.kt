package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutIndexHistoryBottomBinding
import net.knowfx.yaodonghui.entities.IndexHistoryBottomListData
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.fragment.FragmentIndexHistoryBottomVp
import net.knowfx.yaodonghui.ui.views.MyBanner

class IndexHistoryBottomListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    private val mFragments = ArrayList<Fragment>()
    private val mIndexViews = ArrayList<View>()
    private var mCurrentIndex = 0
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexHistoryBottomListData
        val binding = LayoutIndexHistoryBottomBinding.bind(itemView)
        val layoutParams = binding.itemVp.layoutParams
        layoutParams.height = 3 * itemView.resources.getDimensionPixelOffset(R.dimen.dp_40)
        binding.itemVp.layoutParams = layoutParams
        data.list.isNotEmpty().trueLet {
            data.list.forEach {
                mFragments.add(FragmentIndexHistoryBottomVp(it))
            }
            initIndex(binding)
            mFragments.add(0, FragmentIndexHistoryBottomVp(data.list.last()))
            mFragments.add(FragmentIndexHistoryBottomVp(data.list.first()))
            binding.itemVp.addIndexChangeListener {
                switchIndex(binding, it)
            }
            binding.itemVp.initCustom(fragments = mFragments)
        }
    }

    private fun initIndex(binding: LayoutIndexHistoryBottomBinding) {
        repeat(mFragments.size) {
            val view = View(itemView.context)
            val size = itemView.context.resources.getDimensionPixelSize(R.dimen.dp_5)
            val params = LayoutParams(size, size)
            view.background =
                ContextCompat.getDrawable(itemView.context, R.drawable.icon_banner_index_default)
            (it > 0).trueLet {
                params.leftMargin = itemView.resources.getDimensionPixelOffset(R.dimen.dp_4)
            }
            view.layoutParams = params
            mIndexViews.add(view)
            binding.itemIndex.addView(view)
        }
    }

    private fun switchIndex(binding: LayoutIndexHistoryBottomBinding, currentIndex: Int) {
        binding.itemIndex.getChildAt(mCurrentIndex).isSelected = false
        binding.itemIndex.getChildAt(currentIndex).isSelected = true
        mCurrentIndex = currentIndex
    }
}