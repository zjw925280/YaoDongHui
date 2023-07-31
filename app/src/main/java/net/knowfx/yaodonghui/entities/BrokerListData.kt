package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class BrokerListData: BaseListData(LayoutTypes.TYPE_BROKER_LIST) {
    var id = 0
    var label = ""
    var grade = ""
    var name = ""
    var fullName = ""
    var lableNames = ArrayList<String>()
    var logofile = ""
    var logo = ""
    var rowNum = 0
    var isBlock = false
}