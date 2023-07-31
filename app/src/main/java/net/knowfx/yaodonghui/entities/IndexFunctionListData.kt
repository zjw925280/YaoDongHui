package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class IndexFunctionListData : BaseListData(LayoutTypes.TYPE_INDEX_FUNCTION) {

    var indexFunctionList = ArrayList<IndexFunctionData>()

    class IndexFunctionData : BaseListData(LayoutTypes.TYPE_INDEX_FUNCTION_ITEM) {
        var id = 0
        var photo = ""
        var bannerName = ""
        var sort = 0
        var code = ""
    }
}