package net.knowfx.yaodonghui.utils

import android.content.Context
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal

/**
 * @Description:
 * @Author: cbx
 * @CreateDate: 2021/2/26
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/2/26
 * @UpdateRemark: 更新说明
 */
class FileUtils {
    companion object {

        fun getAssetsJson(context: Context, fileName: String): String {
            val stringBuilder = StringBuilder()
            try {
                val assetManager = context.assets
                val bf = BufferedReader(
                    InputStreamReader(
                        assetManager.open(fileName)
                    )
                )
                var line: String?
                while (bf.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }

        /**
         * 获取缓存值
         */
        fun getTotalCacheSize(context: Context): String {
            var cacheSize = getFolderSize(context.cacheDir)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                cacheSize += getFolderSize(context.externalCacheDir)
            }
            return getFormatSize(cacheSize.toDouble())
        }

        /**
         * 清除所有缓存
         */
        fun clearAllCache(context: Context) {
            deleteDir(context.cacheDir)
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                deleteDir(context.externalCacheDir)
                //有网页清理时注意排错，是否存在/data/data/应用package目录下找不到database文件夹的问题
                context.deleteDatabase("webview.db")
                context.deleteDatabase("webviewCache.db")
            }
        }

        /**
         * 删除某个文件
         */
        private fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children: Array<String>? = dir.list()
                children?.run {
                    if (this.isNotEmpty()) {
                        for (i in this.indices) {
                            val success = deleteDir(File(dir, this[i]))
                            if (!success) {
                                return false
                            }
                        }
                    }
                }

                return dir.delete()
            }
            return dir?.delete() ?: false
        }

        /**
         * 获取文件夹大小
         */
        fun getFolderSize(file: File?): Long {
            var size: Long = 0
            if (file != null) {
                val fileList: Array<File> = file.listFiles()
                if (fileList != null && fileList.isNotEmpty()) {
                    for (i in fileList.indices) {
                        // 如果下面还有文件
                        if (fileList[i].isDirectory) {
                            size += getFolderSize(fileList[i])
                        } else {
                            size += fileList[i].length()
                        }
                    }
                }
            }
            return size
        }

        /**
         * 格式化单位
         */
        fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            val megaByte = kiloByte / 1024
            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(megaByte.toString())
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString().toString() + "MB"
            }
            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(gigaByte.toString())
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString().toString() + "GB"
            }
            val result4: BigDecimal = BigDecimal.valueOf(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                .toString() + "TB"
        }
    }
}