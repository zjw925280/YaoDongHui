package net.knowfx.yaodonghui.entities

import android.net.Uri
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes
import java.io.File

class PicData : BaseListData {
    constructor() : super(LayoutTypes.TYPE_PIC_GRID)

    constructor(imagePath: String, type: String) : super(LayoutTypes.TYPE_PIC_GRID) {
        cropUrl = imagePath
        mimeType = type
    }

    constructor(uri: Uri, path: String, type: String) : super(LayoutTypes.TYPE_PIC_GRID) {
        this.uri = uri
        picLocalPath = path
        mimeType = type
    }

    var picLocalPath = ""
    var picServicePath = ""
    var cropUrl = ""
    var uri = Uri.parse("")
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