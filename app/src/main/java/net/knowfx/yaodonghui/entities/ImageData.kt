package net.knowfx.yaodonghui.entities

import android.content.Context
import android.net.Uri
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes
import java.io.File

class ImageData : BaseListData {
    constructor() : super(LayoutTypes.IMAGE_ITEM)

    var picLocalPath = ""
    var picServicePath = ""
    var cropUrl = ""
    var uri =""
    var durationFormat = ""
    var mimeType = ""

    fun isEmpty(): Boolean {
        return picLocalPath.isEmpty()
                && picServicePath.isEmpty()
                && cropUrl.isEmpty()
                && uri.toString().isEmpty()
    }

    fun getLocalFile(): File? {
        return if (picLocalPath.isNotEmpty()) File(picLocalPath) else null
    }
}