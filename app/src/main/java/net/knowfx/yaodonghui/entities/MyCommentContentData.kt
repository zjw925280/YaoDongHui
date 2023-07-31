package net.knowfx.yaodonghui.entities
//{
//    "id": null,
//    "content": "今天真好啊",
//    "createTime": 1685414901000,
//    "model": "ZXH",
//    "title": "继续敛财无法出金！资金盘西城威尔士City Wealth卷土重来！",
//    "coverPicture": "http://app.knowfx.net:8080/profile/upload/2023/05/19/微信截图_20230519171552_20230519171551A468.png",
//    "recreateTime": 1684487755000,
//    "createUser": "",
//    "dealerId": 25
//}
data class MyCommentContentData(
    val content: String = "",
    val createTime: Long = 0L,
    val model: String = "",
    val title: String = "",
    val coverPicture: String = "",
    val recreateTime: String = "",
    val createUser: String = "",
    val dealerId: Int = 0,
)
