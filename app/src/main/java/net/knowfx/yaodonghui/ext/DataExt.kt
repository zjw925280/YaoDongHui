package net.knowfx.yaodonghui.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.knowfx.yaodonghui.base.BaseResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

const val URL_APP_DOWNLOAD = "http://app.knowfx.net:8080/profile/upload/yaodonghui.apk"

inline fun <reified T> Any.result(
    value: T,
    error: (message: String) -> Unit,
    success: (t: T) -> Unit
) {
    val gson = Gson()
    val result = gson.fromJson(gson.toJson(this), BaseResponse::class.java)
    val data = JSONObject(gson.toJson(this)).optString("data")
    if (result.isSuccess()) {
        val typeToken = object : TypeToken<T>() {}
        when (value) {
            is String -> {
                success(data.toString() as T)
            }

            else -> {
                success(gson.fromJson(data, typeToken.type))
            }
        }
    } else {
        error(result.msg)
    }

}

fun HashMap<String, Any>.requestBody(mediaType: MediaType? = null): RequestBody {
    val gson = Gson()
    val requestJson = gson.toJson(this)
    return requestJson.toRequestBody(mediaType ?: "application/json; charset=utf-8".toMediaTypeOrNull())
}

fun HashMap<String, Any>.fileRequestBody(): RequestBody {
    val gson = Gson()
    val requestJson = gson.toJson(this)
    return requestJson.toRequestBody("application/json".toMediaTypeOrNull())
}

fun File.getFileStr(): String {
    var result = ""
    val array = readStream()
    array?.apply {
        result = byte2hex(this) ?: ""
    }

    return result
}

@Throws(Exception::class)
private fun File.readStream(): ByteArray? {
    val fs = FileInputStream(absolutePath)
    val outStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var len: Int
    while (-1 != fs.read(buffer).also { len = it }) {
        outStream.write(buffer, 0, len)
    }
    outStream.close()
    fs.close()
    return outStream.toByteArray()
}

// 二进制转字符串
private fun byte2hex(b: ByteArray): String {
    val sb = StringBuffer()
    var tmp = ""
    for (i in b.indices) {
        tmp = Integer.toHexString(b[i].toInt() and 0XFF)
        if (tmp.length == 1) {
            sb.append("0$tmp")
        } else {
            sb.append(tmp)
        }
    }
    return sb.toString()
}
