package net.knowfx.yaodonghui.utils.imageSelector

import android.Manifest
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.utils.PermissionUtils
import com.lxj.xpopup.XPopup
import net.knowfx.yaodonghui.entities.PicData
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.PickerError
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import com.ypx.imagepicker.builder.MultiPickerBuilder
import com.ypx.imagepicker.data.OnImagePickCompleteListener2
import net.knowfx.yaodonghui.ext.toast
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * @ClassName: SelectImageUtils
 * @Description: java类作用描述
 * @Author: Rain
 * @Version: 1.0
 */
object SelectImageUtils {

    const val MODE_SINGLE = 0
    const val MODE_MULTI = 1
    const val MODE_CAPTURE = 2

    private var presenter: WeChatPresenter? = null

    private var takeListPermission = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var albumListPermission = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun init() {
        if (presenter == null) {
            presenter = WeChatPresenter()
        }
    }

    /**
     * @param isImage 是否只选择图片
     * @param num     选择数量
     * @param isMode  选择模式 0 单一选择 1 多选 3 裁剪
     * @param call    返回图片
     */
    fun dialogImage(
        activity: FragmentActivity,
        isImage: Boolean,
        num: Int,
        isMode: Int,
        call: (list: ArrayList<PicData>?) -> Unit
    ) {
        XPopup.Builder(activity)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .asBottomList(
                "", arrayOf("拍摄", "从手机相册选择")
            )
            { position, _ ->
                when (position) {
                    0 -> PermissionUtils.obtainPermission(activity, takeListPermission) {
                        if (isImage) {
                            if (isMode == SelectMode.MODE_CROP) takePhotoAndCrop(
                                activity,
                                call
                            ) else takePhoto(activity, call)
                        } else {
                            takeVideo(activity, call)
                        }

                    }

                    1 -> PermissionUtils.obtainPermission(activity, albumListPermission) {
                        if (isMode == SelectMode.MODE_CROP) pickAndCrop(activity, call) else pick(
                            activity, isImage, num, isMode, call
                        )
                    }
                }
            }
            .show()
    }

    /**
     * 选择图片带裁剪
     */
    private fun pickAndCrop(
        context: FragmentActivity,
        call: (list: ArrayList<PicData>) -> Unit
    ) {
        val builder: MultiPickerBuilder = ImagePicker.withMulti(presenter) //指定presenter
            .setColumnCount(3) //设置列数
            .mimeTypes(
                EnumSet.of(
                    MimeType.JPEG,
                    MimeType.PNG,
                    MimeType.BMP,
                    MimeType.WEBP
                )
            ) //设置要加载的文件类型，可指定单一类型
            .setSingleCropCutNeedTop(true)
            .showCamera(false) //显示拍照
            .cropSaveInDCIM(false)
            .cropRectMinMargin(0)
            .cropStyle(CropConfig.STYLE_FILL)
            .cropGapBackgroundColor(Color.TRANSPARENT)
            .setCropRatio(1, 1)
        builder.crop(context) { items -> //图片选择回调，主线程
            call(createFileList(items))
        }
    }

    fun pickOnly(
        context: FragmentActivity,
        isImage: Boolean,
        num: Int,
        isMode: Int,
        call: (list: ArrayList<PicData>?) -> Unit
    ) {
        pick(context, isImage, num, isMode, call)
    }

    /**
     * 照片选择
     */
    private fun pick(
        context: FragmentActivity,
        isImage: Boolean,
        num: Int,
        isMode: Int,
        call: (list: ArrayList<PicData>?) -> Unit
    ) {
        val count =
            if (isMode == SelectMode.MODE_SINGLE || isMode == SelectMode.MODE_CROP) 1 else num
//        val isShow = !isImage // 如果只有选择图片则为false 全部类型为true
        ImagePicker.withMulti(presenter)//指定presenter
            .setMaxCount(count)//设置选择的最大数
            .setColumnCount(3)//设置列数
            .setOriginal(true)//是否显示原图
            .mimeTypes(getMienTypes(isImage))//设置要加载的文件类型，可指定单一类型
            .setSelectMode(getSelectMode(isMode))
            .setDefaultOriginal(true)//设置原图选项默认值，true则代表默认打开原图，false代表不打开M
            .setPreviewVideo(true)// 视频是否支持预览
            .showCamera(false)//显示拍照
            .showCameraOnlyInAllMediaSet(false)//是否只在全部媒体相册里展示拍照
            .setPreview(true)//是否开启预览
            .setVideoSinglePick(true)//设置视频单选
            .setSinglePickWithAutoComplete(false)
            .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
//            .setMaxVideoDuration(120000L)//设置视频可选取的最大时长
//            .setMinVideoDuration(5000L)
            .setSingleCropCutNeedTop(true)
            .pick(context, object : OnImagePickCompleteListener2 {
                override fun onPickFailed(error: PickerError?) {}
                override fun onImagePickComplete(items: ArrayList<ImageItem>?) {
                    call(createFileList(items))
                }
            })
    }

    /**
     * 裁剪拍照
     */
    private fun takePhotoAndCrop(
        context: FragmentActivity,
        call: (list: ArrayList<PicData>?) -> Unit
    ) {
        //配置剪裁属性
        val cropConfig = CropConfig()
        cropConfig.setCropRatio(1, 1) //设置剪裁比例
        cropConfig.cropRectMargin = 0 //设置剪裁框间距，单位px
        cropConfig.isCircle = false //是否圆形剪裁
        cropConfig.cropStyle = CropConfig.STYLE_FILL
        cropConfig.cropGapBackgroundColor = Color.TRANSPARENT
        try {
            ImagePicker.takePhotoAndCrop(context, presenter, cropConfig) { items -> //剪裁回调，主线程
                call(createFileList(items))
            }
        } catch (ex: Exception) {
            "获取图片失败".toast()
        }


    }

    /**
     * 拍照
     */
    private fun takePhoto(context: FragmentActivity, call: (list: ArrayList<PicData>?) -> Unit) {
        val imageName = System.currentTimeMillis().toString() + ""
        val isCopyInDCIM = true
        ImagePicker.takePhoto(context, imageName, isCopyInDCIM) { items ->
            call(createFileList(items))
        }
    }

    /**
     * 录视频
     */
    private fun takeVideo(context: FragmentActivity, call: (list: ArrayList<PicData>?) -> Unit) {
        val videoName = System.currentTimeMillis().toString() + ""
        val isCopyInDCIM = true
        ImagePicker.takeVideo(context, videoName, 1200000000L, isCopyInDCIM) { items ->
            call(createFileList(items))
        }
    }

    private fun createFileList(
        items: ArrayList<ImageItem>?
    ): ArrayList<PicData> {
        val list = ArrayList<PicData>()
        if (isBig(items)) {
            "上传图片最大5M,请重新上传".toast()
        } else {
            items?.forEach {
                val cropUrl = it.cropUrl
                if (cropUrl != null && cropUrl.isNotEmpty()) {
                    list.add(PicData(imagePath = cropUrl, type = it.mimeType))
                } else {
                    if (it.uri != null) {
                        val imageBean = PicData(it.uri, it.path, it.mimeType)
                        imageBean.durationFormat = it.durationFormat ?: ""
                        list.add(imageBean)
                    } else {
                        val imageBean = PicData(imagePath = it.path, it.mimeType)
                        imageBean.durationFormat = it.durationFormat ?: ""
                        list.add(imageBean)
                    }
                }
            }
        }
        return list
    }
//
//    private fun upLoadFileList(
//        objectKey: String = "img/default/",
//        items: ArrayList<ImageItem>?
//    ): ArrayList<ImageBean> {
//        val list = ArrayList<ImageBean>()
//        if (isBig(items)) {
//            ToastUtils.showToast("上传图片最大5M,请重新上传")
//        } else {
//            items?.forEach {
//                val cropUrl = it.cropUrl
//                if (cropUrl != null && cropUrl.isNotEmpty()) {
//                    OSSImageSTSUpLoad.instance.upLoadImage(
//                        cropUrl,
//                        "$objectKey${File(cropUrl).name}"
//                    ) { path ->
//                        list.add(ImageBean(imagePath = path))
//                    }
//                } else {
//                    if (it.uri != null) {
//                        OSSImageSTSUpLoad.instance.upLoadImage(
//                            it.uri,
//                            "$objectKey${it.uri.path!!}.png"
//                        ) { path ->
//                            val imageBean = ImageBean(imagePath = path)
//                            imageBean.durationFormat = it.durationFormat ?: ""
//                            list.add(imageBean)
//                        }
//
//                    } else {
//                        OSSImageSTSUpLoad.instance.upLoadImage(
//                            it.path,
//                            "$objectKey${File(it.path).name}"
//                        ) { path ->
//                            val imageBean = ImageBean(imagePath = path)
//                            imageBean.durationFormat = it.durationFormat ?: ""
//                            list.add(imageBean)
//                        }
//                    }
//                }
//            }
//        }
//        return list
//    }

    private fun isBig(items: ArrayList<ImageItem>?): Boolean {
        if (items == null) {
            return false
        }
        var isBig = false
        for (item in items) {
            val folderSize = FileUtils.getFileOrFilesSize(item.path, 3)
            if (folderSize > 5 && !item.isVideo) {
                isBig = true
            }
        }
        return isBig
    }


    private fun uriTurnFile(uri: Uri, context: FragmentActivity): File? {
        val arr = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, arr, null, null, null)
        cursor?.run {
            val imageIndex = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            moveToFirst()
            val path = getString(imageIndex)
            return File(path)
        }

        return null
    }

    /**
     * 选择图片格式
     */
    private fun getSelectMode(isSingle: Int): Int = when (isSingle) {
        0 -> SelectMode.MODE_SINGLE //单选
        1 -> SelectMode.MODE_MULTI//多选
        3 -> SelectMode.MODE_CROP//裁剪
        else -> SelectMode.MODE_MULTI
    }

    /**
     * 选择文件类型
     */
    private fun getMienTypes(isImage: Boolean): Set<MimeType> =
        if (isImage) MimeType.ofImage() else MimeType.ofVideo()

}