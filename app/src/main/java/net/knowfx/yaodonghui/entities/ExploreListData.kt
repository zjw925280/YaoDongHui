package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes
import org.json.JSONObject

class ExploreListData{
    val list = ArrayList<Data>()
    var isFirstPage = false
    var isLastPage = false
    class Data: BaseListData(LayoutTypes.TYPE_EXPLORE_LIST){
        var id = 0
        var title = ""
        var typeName = ""
        var content = ""
        var exposurefile = ""
        var nickname = ""
        var createTime = 0L
        var userhead = ""
        var createUser = ""
        var dealerId = 0
        var dealerName = ""
        var logo = ""
        var type = 0
    }

    fun decodeFromJson(json: JSONObject){
        isFirstPage = json.optBoolean("isFirstPage", false)
        isLastPage = json.optBoolean("isLastPage", false)
    }
}