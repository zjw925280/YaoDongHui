package net.knowfx.yaodonghui.utils

import net.knowfx.yaodonghui.R

object LayoutTypes {
    private const val BASE_TYPE_LAYOUT = 100

    //首页 功能列表
    const val TYPE_INDEX_FUNCTION = BASE_TYPE_LAYOUT + 1

    //首页 功能条目
    const val TYPE_INDEX_FUNCTION_ITEM = BASE_TYPE_LAYOUT + 2

    //首页 Banner
    const val TYPE_INDEX_BANNER = BASE_TYPE_LAYOUT + 3

    //首页 历史标题
    const val TYPE_INDEX_HISTORY_TITLE = BASE_TYPE_LAYOUT + 5

    //首页 历史上部推荐
    const val TYPE_INDEX_HISTORY_TOP = BASE_TYPE_LAYOUT + 6

    //首页 历史上部推荐item
    const val TYPE_INDEX_HISTORY_TOP_ITEM = BASE_TYPE_LAYOUT + 7

    //首页 历史下部热门
    const val TYPE_INDEX_HISTORY_BOTTOM = BASE_TYPE_LAYOUT + 8

    //首页 历史最下方三列表
    const val TYPE_INDEX_PAGER = BASE_TYPE_LAYOUT + 9

    //首页 历史最下方三列表条目
    const val TYPE_INDEX_PAGER_LIST = BASE_TYPE_LAYOUT + 10

    //券商列表item
    const val TYPE_BROKER_LIST = BASE_TYPE_LAYOUT + 13

    //图片显示item
    const val TYPE_PIC_GRID = BASE_TYPE_LAYOUT + 14

    //评论item
    const val TYPE_COMMENT_ITEM = BASE_TYPE_LAYOUT + 15

    //经哥学堂列表item
    const val TYPE_CLASS_LIST = BASE_TYPE_LAYOUT + 16

    //搜索热门列表item
    const val TYPE_SEARCH_HOT_LIST = BASE_TYPE_LAYOUT + 17

    //画汇圈列表item
    const val TYPE_DRAW_CIRCLE_LIST = BASE_TYPE_LAYOUT + 18

    //资讯类共同列表item
    const val TYPE_COMMON_ARTICLE_LIST = BASE_TYPE_LAYOUT + 19

    //监管商列表item
    const val TYPE_SUPERVISE_LIST = BASE_TYPE_LAYOUT + 21

    //曝光列表
    const val TYPE_SUPERVISE_EXPLORE_LIST = BASE_TYPE_LAYOUT + 22

    //评论列表
    const val TYPE_SUPERVISE_COMMENT_LIST = BASE_TYPE_LAYOUT + 23

    //最新新闻列表
    const val TYPE_SUPERVISE_NEWS_LIST = BASE_TYPE_LAYOUT + 24

    //首页 历史下方热门列表item
    const val TYPE_INDEX_HISTORY_BOTTOM_ITEM = BASE_TYPE_LAYOUT + 25

    //全部功能 监管机构item
    const val TYPE_FUNCTION_SUPERVISE_ITEM = BASE_TYPE_LAYOUT + 26

    //曝光列表 item
    const val TYPE_EXPLORE_LIST = BASE_TYPE_LAYOUT + 27

    //底部弹框列表 item
    const val TYPE_DIALOG_LIST = BASE_TYPE_LAYOUT + 28

    //品牌大事件列表 item
    const val TYPE_BRAND_EVENT = BASE_TYPE_LAYOUT + 29

    //版本说明列表 item
    const val TYPE_VERSION_LIST = BASE_TYPE_LAYOUT + 30

    //文章列表 item
    const val TYPE_ARTICLE_LIST = BASE_TYPE_LAYOUT + 31

    const val TYPE_EXPLORE_MINE = BASE_TYPE_LAYOUT + 32
    const val TYPE_DEALER_COMMENT_MINE = BASE_TYPE_LAYOUT + 33
    const val TYPE_OTHER_COMMENT_MINE = BASE_TYPE_LAYOUT + 34
    const val TYPE_SUPERVISE_FILE = BASE_TYPE_LAYOUT + 35
    const val TYPE_SUPERVISE_DEALER = BASE_TYPE_LAYOUT + 36
    const val TYPE_FOCUS_DEALER = BASE_TYPE_LAYOUT + 37
    const val TYPE_FOCUS_ARTICLE = BASE_TYPE_LAYOUT + 38

//    推送历史
    const val TYPE_PUSH_LIST = BASE_TYPE_LAYOUT + 39
    const val TYPE_MSG_LIST = BASE_TYPE_LAYOUT + 40

    //无数据item
    const val TYPE_LAYOUT_EMPTY = BASE_TYPE_LAYOUT + 50
    const val IMAGE_ITEM = BASE_TYPE_LAYOUT + 51

    /**
     * 通过item的类型获取对应的item布局的Map
     */
    val LAYOUT_MAP: HashMap<Int, Int> = hashMapOf(
        Pair(IMAGE_ITEM, R.layout.layout_image_item),
        Pair(TYPE_INDEX_FUNCTION, R.layout.layout_inner_rv),
        Pair(TYPE_INDEX_FUNCTION_ITEM, R.layout.layout_item_index_function),
        Pair(TYPE_INDEX_BANNER, R.layout.layout_index_banner),
        Pair(TYPE_INDEX_HISTORY_TITLE, R.layout.layout_item_index_history_title),
        Pair(TYPE_INDEX_HISTORY_TOP, R.layout.layout_index_history_top),
        Pair(TYPE_INDEX_HISTORY_TOP_ITEM, R.layout.layout_item_index_history_top),
        Pair(TYPE_INDEX_HISTORY_BOTTOM, R.layout.layout_index_history_bottom),
        Pair(TYPE_INDEX_PAGER, R.layout.layout_index_pager),
        Pair(TYPE_INDEX_PAGER_LIST, R.layout.layout_item_supervise_news),
        Pair(TYPE_BROKER_LIST, R.layout.layout_item_broker),
        Pair(TYPE_PIC_GRID, R.layout.layout_item_pic),
        Pair(TYPE_COMMENT_ITEM, R.layout.layout_item_comment),
        Pair(TYPE_CLASS_LIST, R.layout.layout_item_class),
        Pair(TYPE_SEARCH_HOT_LIST, R.layout.layout_item_search_hot),
        Pair(TYPE_DRAW_CIRCLE_LIST, R.layout.layout_item_draw_list),
        Pair(TYPE_COMMON_ARTICLE_LIST, R.layout.layout_item_common_article),
        Pair(TYPE_SUPERVISE_LIST, R.layout.layout_item_supervise),
        Pair(TYPE_SUPERVISE_EXPLORE_LIST, R.layout.layout_item_supervise_explore),
        Pair(TYPE_SUPERVISE_COMMENT_LIST, R.layout.layout_item_supervise_comment),
        Pair(TYPE_SUPERVISE_NEWS_LIST, R.layout.layout_item_supervise_news),
        Pair(TYPE_INDEX_HISTORY_BOTTOM_ITEM, R.layout.layout_item_index_history_bottom),
        Pair(TYPE_FUNCTION_SUPERVISE_ITEM, R.layout.layout_item_index_function),
        Pair(TYPE_EXPLORE_LIST, R.layout.layout_item_explore_list),
        Pair(TYPE_DIALOG_LIST, R.layout.layout_item_dialog_list),
        Pair(TYPE_LAYOUT_EMPTY, R.layout.layout_no_data),
        Pair(TYPE_VERSION_LIST, R.layout.layout_item_version),
        Pair(TYPE_BRAND_EVENT, R.layout.layout_item_brand_event),
        Pair(TYPE_ARTICLE_LIST, R.layout.layout_item_common_article),
        Pair(TYPE_EXPLORE_MINE, R.layout.layout_item_my_post),
        Pair(TYPE_DEALER_COMMENT_MINE, R.layout.layout_item_my_post),
        Pair(TYPE_OTHER_COMMENT_MINE, R.layout.layout_item_my_other_comment),
        Pair(TYPE_SUPERVISE_FILE, R.layout.layout_item_supervise_file),
        Pair(TYPE_SUPERVISE_DEALER, R.layout.layout_item_sup_dealer),
        Pair(TYPE_FOCUS_DEALER, R.layout.layout_item_focus_dealer),
        Pair(TYPE_FOCUS_ARTICLE, R.layout.layout_item_focus_article),
        Pair(TYPE_PUSH_LIST, R.layout.layout_item_push),
        Pair(TYPE_MSG_LIST, R.layout.layout_item_message),
    )
}