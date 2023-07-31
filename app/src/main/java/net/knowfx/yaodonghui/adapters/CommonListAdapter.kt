package net.knowfx.yaodonghui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.viewHolders.*
import net.knowfx.yaodonghui.utils.LayoutTypes
import kotlin.math.max

@Suppress("UNCHECKED_CAST")
class CommonListAdapter<T : BaseListData>(private val itemClick: ((view: View, data: BaseListData, position: Int) -> Unit)? = null) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mDataList = ArrayList<BaseListData>()

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
            LayoutTypes.TYPE_BROKER_LIST -> {
                BrokerListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_PIC_GRID -> {
                PicGridHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_FUNCTION -> {
                IndexFunctionListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_FUNCTION_ITEM -> {
                IndexFunctionHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_BANNER -> {
                IndexBannerListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_HISTORY_TITLE -> {
                IndexHistoryTitleHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_HISTORY_TOP -> {
                IndexHistoryTopListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_HISTORY_TOP_ITEM -> {
                IndexHistoryTopHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_HISTORY_BOTTOM -> {
                IndexHistoryBottomListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_COMMENT_ITEM -> {
                CommentHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_CLASS_LIST -> {
                ClassListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SEARCH_HOT_LIST -> {
                SearchHotHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_DRAW_CIRCLE_LIST -> {
                DrawCircleListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_COMMON_ARTICLE_LIST -> {
                ArticleListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_LIST -> {
                BrokerContentViewHolders.SuperviseHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_EXPLORE_LIST -> {
                BrokerContentViewHolders.ExploreHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_COMMENT_LIST -> {
                BrokerContentViewHolders.CommentHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_NEWS_LIST -> {
                BrokerContentViewHolders.NewsHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_HISTORY_BOTTOM_ITEM -> {
                IndexHistoryBottomItemHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_PAGER -> {
                IndexPagerHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_INDEX_PAGER_LIST -> {
                IndexPagerListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_FUNCTION_SUPERVISE_ITEM -> {
                FunctionSuperviseHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_EXPLORE_LIST -> {
                ExploreListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_DIALOG_LIST -> {
                BottomPopListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_LAYOUT_EMPTY -> {
                NoDataHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_BRAND_EVENT -> {
                BrandEventHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_VERSION_LIST -> {
                VersionHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_ARTICLE_LIST -> {
                ArticleListHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_EXPLORE_MINE -> {
                MyExploreHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_DEALER_COMMENT_MINE -> {
                MyDealerCommentHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_OTHER_COMMENT_MINE -> {
                MyOtherCommentHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_FILE -> {
                SuperviseFileHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_SUPERVISE_DEALER -> {
                SupBrokerHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_FOCUS_DEALER -> {
                FocusDealerHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_FOCUS_ARTICLE -> {
                FocusArticleHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_PUSH_LIST -> {
                PushHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            LayoutTypes.TYPE_MSG_LIST -> {
                MessageHolder(parent, LayoutTypes.LAYOUT_MAP[viewType]!!)
            }

            else -> {
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