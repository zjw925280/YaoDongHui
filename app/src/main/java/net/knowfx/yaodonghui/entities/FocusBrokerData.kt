package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class FocusBrokerData {
    val list = ArrayList<ItemData>()
    val isFirstPage = false
    val isLastPage = false

    class ItemData: BaseListData(LayoutTypes.TYPE_FOCUS_DEALER) {
        var id = 0
        var label = ""
        var grade = ""
        var name = ""
        var fullName = ""
        var lableNames = ArrayList<String>()
        var logofile = ""
        var isSelectMode = false
        var isSelected = false
        var modelId = 0
        var attentionId = 0
    }
}