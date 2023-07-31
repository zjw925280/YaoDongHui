package net.knowfx.yaodonghui.entities

import android.graphics.Bitmap

data class ShareData(
    val iconPath: String = "",
    val title: String = "",
    val content: String = "",
    val url: String = "",
    var tempPath: String = "",
    var percentRemain: String = "",
    var picArray: ByteArray = byteArrayOf(),
    var thumbBitmap: Bitmap? = null,
    var isMine: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShareData

        if (!picArray.contentEquals(other.picArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return picArray.contentHashCode()
    }
}