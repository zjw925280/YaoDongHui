package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class BrandEventData(
    val title: String = "",
    val content: String = "",
    val createTime: Long = 0L
) : BaseListData(LayoutTypes.TYPE_BRAND_EVENT)