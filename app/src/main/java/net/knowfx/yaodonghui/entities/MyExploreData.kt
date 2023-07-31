package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class MyExploreData {
    val list = ArrayList<Data>()
    val isFirstPage = false
    val isLastPage = false
    class Data: BaseListData(LayoutTypes.TYPE_EXPLORE_MINE){
        var id = 0
        var title = ""
        var content = ""
        var dealerId = 0
        var exposurefile = ""
        var createTime = 0L
        var name = ""
        var typeName = ""
        var type = 0
    }
}