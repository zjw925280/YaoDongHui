package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class VersionData(val version: String = "", val content: String = "") :
    BaseListData(LayoutTypes.TYPE_VERSION_LIST)