package net.knowfx.yaodonghui.ext

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import net.knowfx.yaodonghui.BuildConfig
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Long.formatTime(pattern: String? = "yyyy-MM-dd HH:mm"): String {
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(Date(this))
}

fun Long.getCreateFormatTime(): String {
    val arrayCur = System.currentTimeMillis().formatTime("yyyy|MM|dd").split("|")
    val array = this.formatTime("yyyy|MM|dd").split("|")
    return if (arrayCur[0] == array[0]) {
        if (arrayCur[1] == array[1] && arrayCur[2] == array[2]) {
            this@getCreateFormatTime.formatTime("HH:mm")
        } else {
            this@getCreateFormatTime.formatTime("MM-dd HH:mm")
        }
    } else {
        this@getCreateFormatTime.formatTime()
    }
}

fun String.getVideoUrl(): String {
    val index = lastIndexOf("/") + 1
    val clientStr = substring(0, index)
    val fileStr = substring(index, length)
    val coveredFileStr = URLEncoder.encode(fileStr, "UTF-8")
    return clientStr + coveredFileStr
}

fun getTextFromModel(model: String): String {
    return when (model) {
        "ZXH" -> "真相汇"
        "HQST" -> "汇圈神探"
        "DSPH" -> "毒舌评汇"
        "ZXZX" -> "最新资讯"
        "JYSZX" -> "交易商资讯"
        "HHQ" -> "画汇圈"
        "JGXT" -> "经哥学堂"
        "JYS" -> "交易商"
        else -> "未知"
    }
}

fun ArrayList<String>?.getLabelStr(): String {
    this?.apply {
        return getSplitString("|")
    }
    return ""
}

fun ArrayList<*>.getSplitString(splitStr: String = ","): String {
    return if (isNotEmpty()) {
        val stringBuffer = StringBuffer()
        forEach {
            if (it != null) {
                stringBuffer.append(it.toString())
                stringBuffer.append(splitStr)
            }
        }
        stringBuffer.delete(stringBuffer.length - 1, stringBuffer.length)
        stringBuffer.toString()
    } else {
        ""
    }
}

fun TextView.copy() {
    text.toString().copyString(context)
}

fun String.copyString(context: Context) {
    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    //三种数据类型
    // 复制文字到剪切板，其中‘Label’这是任意文字标签ClipData mClipData =ClipData.newPlainText("Label", text);
    // 复制链接url到剪切板，‘Label’这是任意文字标签ClipData mClipData =ClipData.newRawUri("Label", Uri.parse(url));
    // 复制Intent到剪切板，‘Label’这是任意文字标签ClipData mClipData =ClipData.newIntent("Label", intent);
    manager.setPrimaryClip(ClipData.newPlainText("copy", this))
    "内容已经复制到剪贴板".toast()
}

fun String.checkPwd(): Boolean {
    return !(length > 18 || length < 6)
}

fun HashMap<String, Any>.createShareUrl(model: String): String {
    val url = when (model) {
        "ZX" -> "/#/DrawInfo?"
        "BG" -> "/#/room?"
        "JGXT" -> "/#/Lucky_open?"
        "JYS" -> "/#/jysInfo?"
        else -> "/#/NotOpen?"
    }
    val buffer = StringBuffer("${BuildConfig.SERVER_PATH}$url")
    forEach {
        buffer.append(it.key)
        buffer.append("=")
        buffer.append(it.value)
        buffer.append("&")
    }
    return buffer.substring(0, buffer.length - 1)
}

