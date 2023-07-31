package net.knowfx.yaodonghui.entities

import android.content.res.Resources
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class EmptyData(val type: Int = TYPE_NETWORK) : BaseListData(LayoutTypes.TYPE_LAYOUT_EMPTY) {
    companion object {
        const val TYPE_PUSH_MESSAGE = 0
        const val TYPE_SEARCH = 1
        const val TYPE_NETWORK = 2
        const val TYPE_MY_POST = 3
        const val TYPE_FOCUS = 4
    }

    fun getWarningText(res: Resources): String {
        return when (type) {
            TYPE_PUSH_MESSAGE -> {
                res.getString(R.string.string_no_data_message)
            }
            TYPE_SEARCH -> {
                res.getString(R.string.string_no_data_search)
            }
            TYPE_MY_POST -> {
                res.getString(R.string.string_no_data_post)
            }
            TYPE_FOCUS -> {
                res.getString(R.string.string_no_data_focus)
            }

            else -> {
                res.getString(R.string.string_no_data_network)
            }
        }
    }
}