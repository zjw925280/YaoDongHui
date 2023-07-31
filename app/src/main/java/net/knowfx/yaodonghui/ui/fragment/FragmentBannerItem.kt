package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.RoundedCornersTransformation
import net.knowfx.yaodonghui.databinding.LayoutBannerFragmentBinding
import net.knowfx.yaodonghui.entities.IndexBannerListData
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick

class FragmentBannerItem : Fragment() {
    private lateinit var mBinding: LayoutBannerFragmentBinding
    private lateinit var mData: IndexBannerListData.IndexBannerData
    private var mCorners = 0f
    private var mClickListener: ((data: IndexBannerListData.IndexBannerData) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutBannerFragmentBinding.inflate(inflater)
        if (mData.isNotEmpty()) {
            addPic()
        }
        mBinding.picIv.setOnclick {
            mClickListener?.invoke(mData)
        }

        return mBinding.root
    }

    fun setPicPath(
        data: IndexBannerListData.IndexBannerData,
        corners: Float,
        onClick: ((data: IndexBannerListData.IndexBannerData) -> Unit)?
    ) {
        mCorners = corners
        mData = data
        mClickListener = onClick
        if (this::mBinding.isInitialized) {
            addPic()
        }
    }

    private fun addPic() {
        mBinding.picIv.intoCorners(mData.photo.ifEmpty { mData.scrollfile }, radius = mCorners)
    }
}