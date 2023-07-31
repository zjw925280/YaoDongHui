package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class IndexPagerData : BaseListData(LayoutTypes.TYPE_INDEX_PAGER) {

    class PagerListData{
        val list = ArrayList<ListData>()
        val isLastPage = false
        val isFirstPage = true
    }
    class ListData : BaseListData(LayoutTypes.TYPE_INDEX_PAGER_LIST) {
        val id = 0
        val model = ""
        val title = ""
        val createUser = ""
        val createTime = 0L
        val flag = ""
        val coverPicture = ""
        val nickname = ""
    }
}