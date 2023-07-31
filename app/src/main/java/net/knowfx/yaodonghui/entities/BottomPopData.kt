package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class BottomPopData(val iconId: Int = 0, val text: String): BaseListData(LayoutTypes.TYPE_DIALOG_LIST)