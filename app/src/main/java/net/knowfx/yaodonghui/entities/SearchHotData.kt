package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class SearchHotData: BaseListData(LayoutTypes.TYPE_SEARCH_HOT_LIST) {
    var id = 0
    var logo = ""
    var name = ""
    var dealerId = 0
}