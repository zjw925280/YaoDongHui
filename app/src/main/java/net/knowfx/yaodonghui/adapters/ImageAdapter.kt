package net.knowfx.yaodonghui.adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.viewHolders.BaseViewHolder
import net.knowfx.yaodonghui.ui.viewHolders.ImagePicGridHolder
import net.knowfx.yaodonghui.ui.viewHolders.SupBrokerHolder
import net.knowfx.yaodonghui.ui.viewHolders.SuperviseFileHolder
import net.knowfx.yaodonghui.ui.viewHolders.VersionHolder
import net.knowfx.yaodonghui.utils.LayoutTypes

class ImageAdapter <T : BaseListData>(context: Context,private val itemClick: ((view: View, data: BaseListData, position: Int) -> Unit)? = null):
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mDataList = ArrayList<BaseListData>()
     private  var mContext=context
    fun putData(dataList: ArrayList<T>) {
        (mDataList.size > 0).trueLet {
            val size = mDataList.size
            mDataList.clear()
            notifyItemRangeRemoved(0, size)
        }
        mDataList.addAll(dataList)
        notifyItemRangeInserted(0, mDataList.size)
    }

    fun <S : BaseListData> addSingleData(data: S, position: Int) {
        mDataList.add(position, data)
        notifyItemInserted(position)
    }

    fun <S : BaseListData> updateData(data: S, position: Int) {
        if (mDataList[position].javaClass == data.javaClass) {
            mDataList.removeAt(position)
            mDataList.add(position, data)
            notifyItemChanged(position)
        }
    }

    fun <S : BaseListData> addDataListToEnd(dataList: ArrayList<S>) {
        mDataList.addAll(dataList)
        notifyItemRangeInserted(mDataList.size, dataList.size)
    }

    fun delSingleData(position: Int) {
        mDataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun delDataRange(startIndex: Int, size: Int) {
        for (i in 0 until size) {
            mDataList.removeAt(startIndex)
        }
        notifyItemRangeRemoved(startIndex, size)
    }

    fun <S : BaseListData> getDataList(): ArrayList<S> {
        return mDataList as ArrayList<S>
    }

    override fun getItemViewType(position: Int): Int {
        return mDataList[position].layoutType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
//图片显示item
            LayoutTypes.IMAGE_ITEM -> {
                Log.e("点击了","来了")
                ImagePicGridHolder(mDataList,context = mContext,parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }
            else -> {
                Log.e("点击了","错误了")
                DefaultViewHolder(parent, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(mDataList[position], position, itemClick)
    }


    class DefaultViewHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
        override fun onBind(
            data: BaseListData,
            position: Int,
            onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
        ) {
            //TODO 无
        }

    }
}