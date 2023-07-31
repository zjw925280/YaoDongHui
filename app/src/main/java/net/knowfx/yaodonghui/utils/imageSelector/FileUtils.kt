package net.knowfx.yaodonghui.utils.imageSelector

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.DecimalFormat

object FileUtils {
    private val TAG = FileUtils::class.java.simpleName
    const val SIZETYPE_B = 1 //获取文件大小单位为B的double值
    const val SIZETYPE_KB = 2 //获取文件大小单位为KB的double值
    const val SIZETYPE_MB = 3 //获取文件大小单位为MB的double值
    const val SIZETYPE_GB = 4 //获取文件大小单位为GB的double值
    fun saveBitmapToFile(bitmap: Bitmap, toFile: File): String {
        try {
            val fos = FileOutputStream(toFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return toFile.path
    }

    /**
     * 读取assets文件
     *
     * @param context
     * @param fileName
     * @return
     */
    fun getAssetsJson(context: Context, fileName: String?): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName!!)
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
     * 获取视频总时长
     *
     * @param path
     * @return
     */
    fun getVideoDuration(path: String?): Int {
        val player = MediaPlayer()
        try {
            player.setDataSource(path) //recordingFilePath（）为音频文件的路径
            player.prepare()
        } catch (e: Exception) {
            return 0
        }
        val duration = player.duration / 1000 //获取音频的时间
        Log.d("ACETEST", "### duration: $duration")
        player.release() //记得释放资源
        return duration
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String?, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "获取文件大小失败!")
        }
        return FormetFileSize(blockSize, sizeType)
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String?): String {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "获取文件大小失败!")
        }
        return FormetFileSize(blockSize)
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
            Log.e(TAG, "获取文件大小不存在!")
        }
        return size
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            size = if (flist[i].isDirectory) {
                size + getFileSizes(flist[i])
            } else {
                size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private fun FormetFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        fileSizeString = if (fileS < 1024) {
            df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            df.format(fileS.toDouble() / 1024) + "KB"
        } else if (fileS < 1073741824) {
            df.format(fileS.toDouble() / 1048576) + "MB"
        } else {
            df.format(fileS.toDouble() / 1073741824) + "GB"
        }
        return fileSizeString
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private fun FormetFileSize(fileS: Long, sizeType: Int): Double {
        val df = DecimalFormat("#.00")
        var fileSizeLong = 0.0
        when (sizeType) {
            SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
            SIZETYPE_KB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))

            SIZETYPE_MB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))

            SIZETYPE_GB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))

            else -> {}
        }
        return fileSizeLong
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
            return str
        }
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = paramString.substring(i + 1)
        return str
    }
}