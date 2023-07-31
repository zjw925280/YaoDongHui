package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class IndexBannerListData : BaseListData(LayoutTypes.TYPE_INDEX_BANNER) {
    var bannerList = ArrayList<IndexBannerData>()

    class IndexBannerData {
        //{
        //        "id": 6,
        //        "sort": 2,
        //        "url": "http://www.baidu.com",
        //        "photo": "http://8.217.52.84:8080/profile/upload/2023/05/05/微信图片_20230505222023_20230505222058A002.jpg",
        //        "title": "滚动图标题",
        //        "seekId": null,
        //        "model": null,
        //        "createTime": 1679652625000
        //    }
        var photo = ""
        var scrollfile = ""
        var url = ""
        var title = ""
        var seekId = ""
        var model = ""
        fun isNotEmpty(): Boolean  = (photo.isNotEmpty() || scrollfile.isNotEmpty())
    }
}