package net.knowfx.yaodonghui.entities

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class CommentData : BaseListData(LayoutTypes.TYPE_COMMENT_ITEM) {
    val list = ArrayList<Data>()
    val isLastPage = false
    val isFirstPage = true
    val total = 0

    class Data : BaseListData(LayoutTypes.TYPE_COMMENT_ITEM) {
        var nickname = ""
        var content = ""
        var createTime = 0L
        var userhead = ""
        var id = 0
        var createUser = 0
    }
}