package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.RoundedCornersTransformation
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemPicBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ext.setOnclick

class PicGridHolder(view: ViewGroup, id: Int) : BaseViewHolder(view, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as PicData
        val path = when {
            data.picLocalPath.isNotEmpty() -> {
                data.picLocalPath
            }
            data.picServicePath.isNotEmpty() -> {
                data.picServicePath
            }
            else -> {
                ""
            }
        }
        val viewBinding = LayoutItemPicBinding.bind(itemView)
        if (path.isEmpty()) {
            viewBinding.picIv.load(R.mipmap.icon_pic_add)
        } else {
            viewBinding.picIv.load(path) {
                transformations(RoundedCornersTransformation(4f))
            }
        }
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }

}