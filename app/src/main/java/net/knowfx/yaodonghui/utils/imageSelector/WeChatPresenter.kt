package net.knowfx.yaodonghui.utils.imageSelector

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.ext.trueLet
import com.ypx.imagepicker.adapter.PickerItemAdapter
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig
import com.ypx.imagepicker.data.ICameraExecutor
import com.ypx.imagepicker.data.IReloadExecutor
import com.ypx.imagepicker.data.ProgressSceneEnum
import com.ypx.imagepicker.presenter.IPickerPresenter
import com.ypx.imagepicker.utils.PViewSizeUtils
import com.ypx.imagepicker.views.PickerUiConfig
import com.ypx.imagepicker.views.PickerUiProvider
import com.ypx.imagepicker.views.base.*
import com.ypx.imagepicker.views.wx.WXItemView

/**
 * 微信样式选择器Presenter实现类
 */
class WeChatPresenter : IPickerPresenter {
    /**
     * 图片加载，在安卓10上，外部存储的图片路径只能用Uri加载，私有目录的图片可以用绝对路径加载
     * 所以这个方法务必需要区分有uri和无uri的情况
     * 一般媒体库直接扫描出来的图片是含有uri的，而剪裁生成的图片保存在私有目录中，因此没有uri，只有绝对路径
     * 所以这里需要做一个兼容处理
     *
     * @param view        imageView
     * @param item        图片信息
     * @param size        加载尺寸
     * @param isThumbnail 是否是缩略图
     */
    override fun displayImage(view: View, item: ImageItem, size: Int, isThumbnail: Boolean) {
        val urlPath: Any = if (item.uri != null) item.uri else item.path
        val requestBuilder = ImageRequest.Builder(view.context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)//设置内存的缓存策略
            .diskCachePolicy(CachePolicy.ENABLED)//设置磁盘的缓存策略
            .allowRgb565(isThumbnail)
            .networkCachePolicy(CachePolicy.ENABLED)//设置网络的缓存策略
        val screenWidth = view.resources.displayMetrics.widthPixels
        val picSize = (screenWidth - (2 * view.resources.getDimensionPixelOffset(R.dimen.dp_8))) / 3
        (picSize > 0 && isThumbnail).trueLet {
            requestBuilder.size(picSize)
        }
        val imageLoader = ImageLoader.Builder(view.context).build()
        imageLoader.enqueue(requestBuilder.build())
        (view as ImageView).load(urlPath, imageLoader)
    }


    /**
     * 设置自定义ui显示样式，不可返回null
     * 该方法返回一个PickerUiConfig对象
     *
     *
     *
     * 该对象可以配置如下信息：
     * 1.主题色
     * 2.相关页面背景色
     * 3.选择器标题栏，底部栏，item，文件夹列表item，预览页面，剪裁页面的定制
     *
     *
     *
     *
     * 详细使用方法参考 (@link https://github.com/yangpeixing/YImagePicker/blob/master/YPX_ImagePicker_androidx/app/src/main/java/com/ypx/imagepickerdemo/style/WeChatPresenter.java)
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    override fun getUiConfig(context: Context?): PickerUiConfig {
        val uiConfig = PickerUiConfig()
        //设置主题色
        uiConfig.themeColor = Color.parseColor("#09C768")
        //设置是否显示状态栏
        uiConfig.isShowStatusBar = true
        //设置状态栏颜色
        uiConfig.statusBarColor = Color.parseColor("#F5F5F5")
        //设置选择器背景
        uiConfig.pickerBackgroundColor = Color.BLACK
        //设置单图剪裁背景色
        uiConfig.singleCropBackgroundColor = Color.BLACK
        //设置预览页面背景色
        uiConfig.previewBackgroundColor = Color.BLACK
        //设置选择器文件夹打开方向
        uiConfig.folderListOpenDirection = PickerUiConfig.DIRECTION_BOTTOM
        //设置文件夹列表距离顶部/底部边距
        uiConfig.folderListOpenMaxMargin = 0
        //设置小红书剪裁区域的背景色
        uiConfig.cropViewBackgroundColor = Color.BLACK
        //设置文件夹列表距离底部/顶部的最大间距。通俗点就是设置文件夹列表的高
        if (context != null) {
            uiConfig.folderListOpenMaxMargin = PViewSizeUtils.dp(context, 100f)
        }

        //自定义选择器标题栏，底部栏，item，文件夹列表item，预览页面，剪裁页面
        uiConfig.pickerUiProvider = object : PickerUiProvider() {
            //定制选择器标题栏，默认实现为 WXTitleBar
            override fun getTitleBar(context: Context): PickerControllerView {
                return super.getTitleBar(context)
            }

            //定制选择器底部栏，返回null即代表没有底部栏，默认实现为 WXBottomBar
            override fun getBottomBar(context: Context): PickerControllerView {
                return super.getBottomBar(context)
            }

            //定制选择器item,默认实现为 WXItemView
            override fun getItemView(context: Context): PickerItemView {
                val itemView = super.getItemView(context) as WXItemView
                itemView.setBackgroundColor(Color.parseColor("#303030"))
                return itemView
            }

            //定制选择器文件夹列表item,默认实现为 WXFolderItemView
            override fun getFolderItemView(context: Context): PickerFolderItemView {
                return super.getFolderItemView(context)
            }

            //定制选择器预览页面,默认实现为 WXPreviewControllerView
            override fun getPreviewControllerView(context: Context): PreviewControllerView {
                return super.getPreviewControllerView(context)
            }

            //定制选择器单图剪裁页面,默认实现为 WXSingleCropControllerView
            override fun getSingleCropControllerView(context: Context): SingleCropControllerView {
                return super.getSingleCropControllerView(context)
            }
        }
        return uiConfig
    }

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    override fun tip(context: Context?, msg: String) {
        if (context == null) {
            return
        }
        Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    override fun overMaxCountTip(context: Context?, maxCount: Int) {
        tip(context, "最多选择" + maxCount + "个文件")
    }

    /**
     * 显示loading加载框，注意需要调用show方法
     *
     * @param activity          启动加载框的activity
     * @param progressSceneEnum [ProgressSceneEnum]
     *
     *
     *
     * 当progressSceneEnum==当ProgressSceneEnum.loadMediaItem 时，代表在加载媒体文件时显示加载框
     * 目前框架内规定，当文件夹内媒体文件少于1000时，强制不显示加载框，大于1000时才会执行此方法
     *
     *
     *
     * 当progressSceneEnum==当ProgressSceneEnum.crop 时，代表是剪裁页面的加载框
     *
     * @return DialogInterface 对象，用于关闭加载框，返回null代表不显示加载框
     */
    @Suppress("DEPRECATION")
    override fun showProgressDialog(
        activity: Activity?,
        progressSceneEnum: ProgressSceneEnum
    ): DialogInterface {
        return ProgressDialog.show(
            activity,
            null,
            if (progressSceneEnum == ProgressSceneEnum.crop) "正在剪裁..." else "正在加载..."
        )
    }

    /**
     * 拦截选择器完成按钮点击事件
     *
     * @param activity     当前选择器activity
     * @param selectedList 已选中的列表
     * @return true:则拦截选择器完成回调， false，执行默认的选择器回调
     */
    override fun interceptPickerCompleteClick(
        activity: Activity?,
        selectedList: ArrayList<ImageItem>,
        selectConfig: BaseSelectConfig
    ): Boolean {
//        if (MainActivity.isAutoJumpAlohaActivity) {
//            tip(activity, "拦截了完成按钮点击" + selectedList.size());
//            Intent intent = new Intent(activity, AlohaActivity.class);
//            intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, selectedList);
//            activity.startActivity(intent);
//            return true;
//        }
        return false
    }

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    override fun interceptPickerCancel(
        activity: Activity?,
        selectedList: ArrayList<ImageItem>
    ): Boolean {
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            return false
        }
        activity.finish()
        //        AlertDialog.Builder builder = new AlertDialog.Builder(new WeakReference<>(activity).get());
//        builder.setMessage("是否放弃选择？");
//        builder.setPositiveButton(R.string.picker_str_sure,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        activity.finish();
//                    }
//                });
//        builder.setNegativeButton(R.string.picker_str_error,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
        return true
    }

    /**
     *
     *
     * 图片点击事件拦截，如果返回true，则不会执行选中操纵，如果要拦截此事件并且要执行选中
     * 请调用如下代码：
     *
     *
     * adapter.preformCheckItem()
     *
     *
     *
     *
     * 此方法可以用来跳转到任意一个页面，比如自定义的预览
     *
     * @param activity        上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param selectConfig    选择器配置项，如果是微信样式，则selectConfig继承自MultiSelectConfig
     * 如果是小红书剪裁样式，则继承自CropSelectConfig
     * @param adapter         当前列表适配器，用于刷新数据
     * @param isClickCheckBox 是否点击item右上角的选中框
     * @param reloadExecutor  刷新器
     * @return 是否拦截
     */
    override fun interceptItemClick(
        activity: Activity?,
        imageItem: ImageItem,
        selectImageList: ArrayList<ImageItem>,
        allSetImageList: ArrayList<ImageItem>,
        selectConfig: BaseSelectConfig,
        adapter: PickerItemAdapter,
        isClickCheckBox: Boolean,
        reloadExecutor: IReloadExecutor?
    ): Boolean {
        return false
    }

    /**
     * 拍照点击事件拦截
     *
     * @param activity  当前activity
     * @param takePhoto 拍照接口
     * @return 是否拦截
     */
    override fun interceptCameraClick(activity: Activity?, takePhoto: ICameraExecutor): Boolean {
        if (activity == null || activity.isDestroyed) {
            return false
        }
        val builder = AlertDialog.Builder(activity)
        builder.setSingleChoiceItems(arrayOf("拍照", "录像"), -1) { dialog, which ->
            dialog.dismiss()
            if (which == 0) {
                takePhoto.takePhoto()
            } else {
                takePhoto.takeVideo()
            }
        }
        builder.show()
        return true
    }
}