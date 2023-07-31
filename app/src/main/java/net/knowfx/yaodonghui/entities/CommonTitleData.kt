package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class CommonTitleData(val titleString: String, val isShowMore: Boolean) :
    BaseListData(LayoutTypes.TYPE_INDEX_HISTORY_TITLE)