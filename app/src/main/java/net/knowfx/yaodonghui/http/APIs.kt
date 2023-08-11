package net.knowfx.yaodonghui.http

object APIs {

    /***********获取图形码***********/
    const val URL_GETCODE = "ad/captchaImage"
    /***********账号相关***********/
    const val URL_REGISTER = "admin/sys/user/register"
    const val URL_GET_PHONE_UUID_CODE = "api/admin/sys/user/sendSmsByPhone"
//    const val URL_GET_PHONE_CODE = "admin/sys/user/sendSmsByPhone"
    const val URL_FORGET_PWD = "admin/sys/user/setNewPassword"
    const val URL_LOGIN_PWD = "admin/sys/user/login"
    const val URL_LOGIN_CODE = "admin/sys/user/loginByPhone"
    const val URL_CHANGE_PWD = "admin/sys/user/changePassword"
    const val URL_LOGOUT = "admin/sys/user/logout"
    const val URL_DELETE_ACCOUNT = "admin/sys/user/changeCancel"


    /***********首页相关***********/
    const val URL_GET_INDEX_FUNCTION = "class/list"
    const val URL_GET_INDEX_BANNER = "roll/list"
    const val URL_GET_INDEX_HIS_TOP = "GlanceHistoryInfo/findBrowsehistory"
    const val URL_GET_INDEX_HIS_BOTTOM = "GlanceHistoryInfo/findPopularityList"
    const val URL_GET_INDEX_PAGER_LIST = "SeekInfo/SeekInfo/findSeekList"
    const val URL_GET_FUNCTION_ALL = "class/findbannerClassAndRegulators"
    const val URL_GET_UNREAD = "/Message/noReadyCount"

    /***********交易商***********/
    const val URL_BRAND_LIST = "dealer/findBrandList"
    const val URL_BLOCK_LIST = "dealer/findblackList"
    const val URL_DEALER_LIST = "cuser/findDealerList"
    const val URL_SUPERVISE_LIST = "dealer/findSuperviseList"
    const val URL_SUPERVISE_INFO = "regulators/getDetailsById"
    const val URL_SUPERVISE_PROFILE = "regulators/getlogofullNameAndProper"
    const val URL_SUPERVISE_DEALER = "dealer/findListByReguId"


    /***********监管商***********/
    const val URL_EXPLORE_LIST = "exposure/findListByType"
    const val URL_EXPLORE_POST = "exposure/add"
    const val URL_COMMENT_POST = "ReviewInfo/add"
    const val URL_EXPLORE_CONTENT = "exposure/getExposureDetailsById"
    const val URL_COMMENT_CONTENT = "ReviewInfo/getReviewDealerDetalis"
    const val URL_GET_EXPLORE_LIST_SEARCH = "exposure/findBySearch"


    /***********经哥学堂***********/
    const val URL_CLASS_LIST = "school/findList"
    const val URL_CLASS_CONTENT = "school/getSchoolDetails"
    const val URL_CLASS_COMMENT = "school/getReviewDTO"


    /***********搜索***********/
    const val URL_DEALER_SEARCH = "dealer/findDearlerBySearch"
    const val URL_DEALER_HOT = "demandsearch/list"

    /***********设置***********/
    const val URL_CHANGE_AVATAR = "cuser/edit"
    const val URL_CHANGE_NICKNAME = "cuser/update"
    const val URL_CHANGE_SEX = "cuser/changeSexual"
    const val URL_GET_BRAND_EVENT = "BrandHistoryInfoa/findAll"
    const val URL_GET_VERSIONS = "VersionHistoryInfo/findAll"
    const val URL_GET_PROFILE = "BriefHistoryInfo/query"

    /***********交易商***********/
    const val URL_DEALER_CONTENT = "dealer/getDearlerDetailsById"
    const val URL_GET_HELP = "config/getDetailsById"
    const val URL_GET_DEALER_ARTICLE = "dealer/getDearlerZxlist"
    const val URL_POST_HELP = "LegalAidInfo/add"

    /***********文章(画汇圈，真相汇，汇圈神探，毒蛇评汇)***********/
    /**获取文章详情*/
    const val URL_ARTICLE_CONTENT = "SeekInfo/SeekInfo/findSeekDetails"
    const val URL_DRAW_CIRCLE_LIST = "circle/findCircleList"
    const val URL_GET_TRUTH_LIST = "FaceInfo/findFaceList"//获取真相汇列表
    const val URL_GET_SHERLOCK_LIST = "DetectiveInfo/findDetectiveList"//获取汇圈神探列表
    const val URL_GET_SNAKE_LIST = "TongueInfo/findTongueList"//获取毒蛇评汇列表
    const val URL_GET_COMMENT = "SeekInfo/SeekInfo/getReviewDTO"//获取评论列表

    /***********实名认证***********/
    const val URL_SUBMIT = "cuser/realName"

    /***********我的发布***********/
    const val URL_MY_EXPLORE_POST = "cuser/findExposureList"
    const val URL_MY_DEALER_COMMENT_POST = "cuser/findMyReviewInfoList"
    const val URL_MY_OTHER_COMMENT_POST = "cuser/findothercommentList?status="

    /***********我的关注***********/
    const val URL_MY_FOCUS_DEALER = "cuser/findDealerList"
    const val URL_MY_FOCUS_ARTICLE = "cuser/findSeekList"

    /***********历史相关***********/
    const val URL_HISTORY_TOP = "dealer/findHistoryRecordByPopula"
    const val URL_HISTORY_SEE = "dealer/findHistoryRecordByTotalvisits"
    const val URL_HISTORY_MY = "cuser/findHistoryRecord"
    const val URL_HISTORY_MY_DEL = "GlanceHistoryInfo/remove"

    /***********商务合作***********/
    const val URL_BUSINESS_COP = "CooperateInfo/add"

    /***********消息相关***********/
    const val URL_PUSH_HISTORY = "PropeHistoryInfo/findAll"
    const val URL_MSG_HISTORY = "Message/findAll"
    const val URL_NEW_MESSAGE = "Message/findNewMessageList"

    /***********公共接口***********/
    const val URL_COMMENT_POST_COMMON = "SimpleReviewInfo/add"
    const val URL_COMMENT_DEL_COMMON = "SimpleReviewInfo/remove"
    const val URL_FOLLOW_COMMON = "InterestInfo/add"
    const val URL_UN_FOLLOW_COMMON = "InterestInfo/remove"
    const val URL_GET_USER_INFO = "cuser/getUserInfo"
    const val URL_REFRESH_TOKEN = "admin/sys/user/refreshTaken"
    const val URL_FEEDBACK = "FeedbackInfo/add"
    const val URL_GET_CONTRACT = "SysLabelConfig/SysLabelConfig/findByType"
    const val URL_SPLASH_AD = "banner/advert"
    const val URL_GET_COMMENT_CONTENT = "SimpleReviewInfo/getOtherCommentDetailsById"//获取评论详情
}