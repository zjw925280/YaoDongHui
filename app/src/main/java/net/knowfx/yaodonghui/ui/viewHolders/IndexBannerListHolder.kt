package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutIndexBannerBinding
import net.knowfx.yaodonghui.entities.IndexBannerListData

class IndexBannerListHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    private val mBinding = LayoutIndexBannerBinding.bind(itemView)
    private val mDataList = ArrayList<IndexBannerListData.IndexBannerData>()

    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexBannerListData
        mDataList.clear()
        mBinding.layoutIndex.removeAllViews()
        mDataList.addAll(data.bannerList)
        initIndex()
        val width =
            itemView.context.resources.displayMetrics.widthPixels - (2 * itemView.context.resources.getDimensionPixelOffset(
                R.dimen.dp_12
            ))
        val height = width * 3 / 7
        val params =
            mBinding.viewBanner.layoutParams ?: ConstraintLayout.LayoutParams(width, height)
        params.width = width
        params.height = height
        mBinding.viewBanner.layoutParams = params
        mBinding.viewBanner.addIndexChangeListener {
            switchIndex(it)
        }
        mBinding.viewBanner.initBanner(mDataList)
        switchIndex(0)
    }

    private var mCurrentPosition = 0
    private fun switchIndex(position: Int) {
        mBinding.layoutIndex.getChildAt(mCurrentPosition).isSelected = false
        mBinding.layoutIndex.getChildAt(position).isSelected = true
        mCurrentPosition = position
    }

    private fun initIndex() {
        val width = itemView.resources.getDimensionPixelOffset(R.dimen.dp_20)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        val distance = itemView.resources.getDimensionPixelOffset(R.dimen.dp_2)
        when (mDataList.size) {
            0, 1 -> {
                val view = View(itemView.context)
                val params = LayoutParams(width, height)
                view.background = ContextCompat.getDrawable(
                    itemView.context, R.drawable.bg_banner_index_middle
                )
                view.layoutParams = params
                mBinding.layoutIndex.addView(view)
            }

            2 -> {
                val viewLeft = View(itemView.context)
                val paramsL = LayoutParams(width, height)
                paramsL.rightMargin = distance
                viewLeft.background = ContextCompat.getDrawable(
                    itemView.context, R.drawable.bg_banner_index_first
                )
                viewLeft.layoutParams = paramsL
                mBinding.layoutIndex.addView(viewLeft)

                val viewRight = View(itemView.context)
                val paramsR = LayoutParams(width, height)
                viewRight.background = ContextCompat.getDrawable(
                    itemView.context, R.drawable.bg_banner_index_last
                )
                viewRight.layoutParams = paramsR
                mBinding.layoutIndex.addView(viewRight)
            }

            else -> {
                for (i in 0 until mDataList.size) {
                    val view = View(itemView.context)
                    val params = LayoutParams(width, height)
                    when (i) {
                        0 -> {
                            params.rightMargin = distance
                            view.background = ContextCompat.getDrawable(
                                itemView.context, R.drawable.bg_banner_index_first
                            )
                        }

                        mDataList.size - 1 -> {
                            view.background =
                                ContextCompat.getDrawable(
                                    itemView.context,
                                    R.drawable.bg_banner_index_last
                                )
                        }

                        else -> {
                            params.rightMargin = distance
                            view.background = ContextCompat.getDrawable(
                                itemView.context, R.drawable.bg_banner_index_middle
                            )
                        }
                    }
                    view.layoutParams = params
                    mBinding.layoutIndex.addView(view)
                }
            }
        }

    }
}