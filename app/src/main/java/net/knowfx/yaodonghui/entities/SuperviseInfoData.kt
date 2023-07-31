package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class SuperviseInfoData {
    val files = ArrayList<String>()
    val id = 0
    val licensedlogo = ""
    val website = ""

    fun getFileList(): ArrayList<FileData> {
        val fileList = ArrayList<FileData>()
        for (i in 1..files.size) {
            val data = FileData()
            data.name = "附件$i"
            data.url = files[i - 1]
            fileList.add(data)
        }
        return fileList
    }

    class FileData : BaseListData(LayoutTypes.TYPE_SUPERVISE_FILE) {
        var url = ""
        var name = ""
    }
}