package net.knowfx.yaodonghui.utils

import net.knowfx.yaodonghui.ext.trueLet

/**
 * 曝光类型的枚举类
 */
enum class ExploreTypeEnum(val title: String, val value: Int) {
    TYPE_EXPLORE_ALL("全部", 0),
    TYPE_EXPLORE_NO_CASH("无法出金", 1),
    TYPE_EXPLORE_DROP_FAST("滑点严重", 2),
    TYPE_EXPLORE_CHEAT("诱导诈骗", 3),
    TYPE_EXPLORE_OTHER("其它曝光", 4),
}

fun getExploreTitleWithValue(value: Int): String{
    ExploreTypeEnum.values().forEach {
        if (it.value == value) {
            return it.title
        }
    }
    return "未知"
}