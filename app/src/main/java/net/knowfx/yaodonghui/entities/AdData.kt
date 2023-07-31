package net.knowfx.yaodonghui.entities

//{
//    "params": {},
//    "id": 13,
//    "imageUrl": "http://app.knowfx.net:8080/profile/upload/2023/05/25/1684146576161_20230525225419A001.jpg",
//    "jumpType": "2",
//    "jumpUrl": "",
//    "model": "JYSZX",
//    "seekId": 100039,
//    "showTime": 6
//}
const val JUMP_TYPE_NOTHING = "0"
const val JUMP_TYPE_URL = "1"
const val JUMP_TYPE_CONTENT = "2"

data class AdData(
    val id: Int = 0,
    val imageUrl: String = "",
    val jumpType: String = "",
    val jumpUrl: String = "",
    val model: String = "",
    val seekId: Int = 0,
    val showTime: Int = 0
)