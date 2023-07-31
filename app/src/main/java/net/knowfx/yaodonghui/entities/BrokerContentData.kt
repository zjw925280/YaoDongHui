package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class BrokerContentData {
    /**交易商名称*/
    var fullName = ""

    /**交易商logo*/
    var logo = ""

    /**交易商logo缩略图*/
    var thumbLogo = ""

    /**交易商评分*/
    var grade = 0.00

    /**交易环境*/
    var businessval = ""

    /**邮箱*/
    var email = ""

    /**网站*/
    var website = ""

    /**电话*/
    var phone = ""

    /**鉴定*/
    var appraisalval = ""

    /**国家国旗*/
    var countrylogo = ""

    /**标签集合*/
    var lableNames = ArrayList<String>()

    var riskGrade = 0

    /**风险内容*/
    var riskDescription = ArrayList<String>()

    /**是否关注*/
    var follow = false

    /**是否展示平台援助*/
    var ishelp = 1

    /**是否展示曝光列表*/
    var isshow = 1

    /**是否显示评论列表*/
    var iscomment = 1

    /**交易商监管状态*/
    var label = ""

    /**网址列表*/
    var websites = ArrayList<String>()

    /**Banner数据*/
    var dealerScrollDTOS = ArrayList<IndexBannerListData.IndexBannerData>()

    /**监管商列表*/
    var regulatorsListDTOList = ArrayList<SuperviseData>()

    /**曝光列表*/
    var exposureInfoDTOList = ArrayList<ExploreData>()

    /**评论列表*/
    var reviewDTOList = ArrayList<CommentData>()

    class SuperviseData : BaseListData(LayoutTypes.TYPE_SUPERVISE_LIST) {
        var id = 0
        var flagPath = ""
        var name = ""
        var label = ""
        var countrylogo = ""
        var typeName = ""
    }

    class ExploreData : BaseListData(LayoutTypes.TYPE_SUPERVISE_EXPLORE_LIST) {

        var id = ""
        var type = ""
        var title = ""
        var userhead = ""
        var nickname = ""
        var createTime = 0L
    }

    class CommentData : BaseListData(LayoutTypes.TYPE_SUPERVISE_COMMENT_LIST) {

        var id = ""
        var userhead = ""
        var nickname = ""
        var content = ""
        var createTime = 0L
    }

    data class BannerData(
        var url: String = "",
        var scrollfile: String = ""
    )
}