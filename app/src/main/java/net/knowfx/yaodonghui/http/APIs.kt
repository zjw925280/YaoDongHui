package net.knowfx.yaodonghui.http

object APIs {

    /***********获取图形码***********/
    const val URL_GETCODE = "ad/captchaImage"
    /***********账号相关***********/
    const val URL_REGISTER = "api/admin/sys/user/register"
    const val URL_GET_PHONE_UUID_CODE = "api/admin/sys/user/sendSmsByPhone"
//    const val URL_GET_PHONE_CODE = "admin/sys/user/sendSmsByPhone"
    const val URL_FORGET_PWD = "api/admin/sys/user/setNewPassword"
    const val URL_LOGIN_PWD = "api/admin/sys/user/login"
    const val URL_LOGIN_CODE = "api/admin/sys/user/loginByPhone"
    const val URL_CHANGE_PWD = "api/admin/sys/user/changePassword"
    const val URL_LOGOUT = "api/admin/sys/user/logout"
    const val URL_DELETE_ACCOUNT = "api/admin/sys/user/changeCancel"


    /***********首页相关***********/
    const val URL_GET_INDEX_FUNCTION = "api/class/list"
    const val URL_GET_INDEX_BANNER = "api/roll/list"
    const val URL_GET_INDEX_HIS_TOP = "api/GlanceHistoryInfo/findBrowsehistory"
    const val URL_GET_INDEX_HIS_BOTTOM = "api/GlanceHistoryInfo/findPopularityList"
    const val URL_GET_INDEX_PAGER_LIST = "api/SeekInfo/SeekInfo/findSeekList"
    const val URL_GET_FUNCTION_ALL = "api/class/findbannerClassAndRegulators"
    const val URL_GET_UNREAD = "/api/Message/noReadyCount"

    /***********交易商***********/
    const val URL_BRAND_LIST = "api/dealer/findBrandList"
    const val URL_BLOCK_LIST = "api/dealer/findblackList"
    const val URL_DEALER_LIST = "api/cuser/findDealerList"
    const val URL_SUPERVISE_LIST = "api/dealer/findSuperviseList"
    const val URL_SUPERVISE_INFO = "api/regulators/getDetailsById"
    const val URL_SUPERVISE_PROFILE = "api/regulators/getlogofullNameAndProper"
    const val URL_SUPERVISE_DEALER = "api/dealer/findListByReguId"


    /***********监管商***********/
    const val URL_EXPLORE_LIST = "api/exposure/findListByType"
    const val URL_EXPLORE_POST = "api/exposure/add"
    const val URL_COMMENT_POST = "api/ReviewInfo/add"
    const val URL_EXPLORE_CONTENT = "api/exposure/getExposureDetailsById"
    const val URL_COMMENT_CONTENT = "api/ReviewInfo/getReviewDealerDetalis"
    const val URL_GET_EXPLORE_LIST_SEARCH = "api/exposure/findBySearch"


    /***********经哥学堂***********/
    const val URL_CLASS_LIST = "api/school/findList"
    const val URL_CLASS_CONTENT = "api/school/getSchoolDetails"
    const val URL_CLASS_COMMENT = "api/school/getReviewDTO"


    /***********搜索***********/
    const val URL_DEALER_SEARCH = "api/dealer/findDearlerBySearch"
    const val URL_DEALER_HOT = "api/demandsearch/list"

    /***********设置***********/
    const val URL_CHANGE_AVATAR = "api/cuser/edit"
    const val URL_CHANGE_NICKNAME = "api/cuser/update"
    const val URL_CHANGE_SEX = "api/cuser/changeSexual"
    const val URL_GET_BRAND_EVENT = "api/BrandHistoryInfoa/findAll"
    const val URL_GET_VERSIONS = "api/VersionHistoryInfo/findAll"
    const val URL_GET_PROFILE = "api/BriefHistoryInfo/query"

    /***********交易商***********/
    const val URL_DEALER_CONTENT = "api/dealer/getDearlerDetailsById"
    const val URL_GET_HELP = "api/config/getDetailsById"
    const val URL_GET_DEALER_ARTICLE = "api/dealer/getDearlerZxlist"
    const val URL_POST_HELP = "api/LegalAidInfo/add"

    /***********文章(画汇圈，真相汇，汇圈神探，毒蛇评汇)***********/
    /**获取文章详情*/
    const val URL_ARTICLE_CONTENT = "api/SeekInfo/SeekInfo/findSeekDetails"
    const val URL_DRAW_CIRCLE_LIST = "api/circle/findCircleList"
    const val URL_GET_TRUTH_LIST = "api/FaceInfo/findFaceList"//获取真相汇列表
    const val URL_GET_SHERLOCK_LIST = "api/DetectiveInfo/findDetectiveList"//获取汇圈神探列表
    const val URL_GET_SNAKE_LIST = "api/TongueInfo/findTongueList"//获取毒蛇评汇列表
    const val URL_GET_COMMENT = "api/SeekInfo/SeekInfo/getReviewDTO"//获取评论列表

    /***********实名认证***********/
    const val URL_SUBMIT = "api/cuser/realName"

    /***********我的发布***********/
    const val URL_MY_EXPLORE_POST = "api/cuser/findExposureList"
    const val URL_MY_DEALER_COMMENT_POST = "api/cuser/findMyReviewInfoList"
    const val URL_MY_OTHER_COMMENT_POST = "api/cuser/findothercommentList?status="

    /***********我的关注***********/
    const val URL_MY_FOCUS_DEALER = "api/cuser/findDealerList"
    const val URL_MY_FOCUS_ARTICLE = "api/cuser/findSeekList"

    /***********历史相关***********/
    const val URL_HISTORY_TOP = "api/dealer/findHistoryRecordByPopula"
    const val URL_HISTORY_SEE = "api/dealer/findHistoryRecordByTotalvisits"
    const val URL_HISTORY_MY = "api/cuser/findHistoryRecord"
    const val URL_HISTORY_MY_DEL = "api/GlanceHistoryInfo/remove"

    /***********商务合作***********/
    const val URL_BUSINESS_COP = "api/CooperateInfo/add"

    /***********消息相关***********/
    const val URL_PUSH_HISTORY = "api/PropeHistoryInfo/findAll"
    const val URL_MSG_HISTORY = "api/Message/findAll"
    const val URL_NEW_MESSAGE = "api/Message/findNewMessageList"

    /***********公共接口***********/
    const val URL_COMMENT_POST_COMMON = "api/SimpleReviewInfo/add"
    const val URL_COMMENT_DEL_COMMON = "api/SimpleReviewInfo/remove"
    const val URL_FOLLOW_COMMON = "api/InterestInfo/add"
    const val URL_UN_FOLLOW_COMMON = "api/InterestInfo/remove"
    const val URL_GET_USER_INFO = "api/cuser/getUserInfo"
    const val URL_REFRESH_TOKEN = "api/admin/sys/user/refreshTaken"
    const val URL_FEEDBACK = "api/FeedbackInfo/add"
    const val URL_GET_CONTRACT = "api/SysLabelConfig/SysLabelConfig/findByType"
    const val URL_SPLASH_AD = "api/banner/advert"
    const val URL_GET_COMMENT_CONTENT = "api/SimpleReviewInfo/getOtherCommentDetailsById"//获取评论详情
}