package net.knowfx.yaodonghui.ui.viewHolders

import android.content.Context
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutImageItemBinding
import net.knowfx.yaodonghui.entities.ImageData
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.activity.ExploreCommentContentActivity
import net.knowfx.yaodonghui.ui.activity.Imagectivity

class ImagePicGridHolder (dataList: ArrayList<BaseListData>,context:Context,view: ViewGroup, id: Int) : BaseViewHolder(view, id) {

   var mContext=context
    var mdataList=dataList

    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
            data as ImageData
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
        val viewBinding = LayoutImageItemBinding.bind(itemView)
        if (path.isEmpty()) {
            viewBinding.photoView.load(R.mipmap.icon_pic_add)

        } else {
            viewBinding.photoView.load(path) {
                transformations(RoundedCornersTransformation(4f))
            }

        }

        viewBinding.photoView.setOnclick {
            //跳转到曝光详情
            jumpToTarget(
                mContext,
                Imagectivity::class.java,
                hashMapOf(
                    Pair("url",Gson().toJson(mdataList)),
                )
            )



        }


        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)

        }
    }
}

