package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class ArticleListData {
    val list = ArrayList<ListData>()
    val isLastPage = false
    val isFirstPage = true

    data class ListData(
        val id: Int = 0,                  //id
        val title: String = "",           //标题
        val coverPicture: String = "",    //封面图
        val nickname: String = "",        //用户昵称
        val createTime: Long = 0L,        //创建时间时间戳
        val userhead: String = "",        //用户头像
        val createUser: String = "",      //创建用户id
        val model: String = "",           //所属模块
        val subtitle: String = "",        //副标题
        val divide: String = "",          //所属模块（备用）
    ) : BaseListData(LayoutTypes.TYPE_ARTICLE_LIST)
}