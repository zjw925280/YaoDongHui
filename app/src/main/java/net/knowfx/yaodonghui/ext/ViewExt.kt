package net.knowfx.yaodonghui.ext

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.FOCUS_UP
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.LayoutCommentBarBinding
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.ui.dialogs.LoadingDialog
import net.knowfx.yaodonghui.utils.ClickControlUtil
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.RefreshController
import net.knowfx.yaodonghui.utils.ToastUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil


fun View.setOnclick(click: (v: View) -> Unit) {
    this.setOnClickListener {
        ClickControlUtil.getInstance().isClickable().trueLet {
            click.invoke(it)
        }
    }
}

fun setMultipleClick(vararg vList: View, click: (v: View) -> Unit) {
    vList.forEach {
        it.setOnClickListener { v ->
            ClickControlUtil.getInstance().isClickable().trueLet {
                click.invoke(v)
            }
        }
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun Window.totalFullScreen(controller: WindowInsetsControllerCompat) {
    //设置状态栏文字颜色, 隐藏底部导航栏
    //设置允许Window在危险区域绘制
    val layoutParams = attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        layoutParams.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    attributes = layoutParams
    controller.hide(WindowInsetsCompat.Type.navigationBars())
    WindowCompat.setDecorFitsSystemWindows(this, false)
    this.statusBarColor = Color.TRANSPARENT
    this.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE)
}

fun Window.fullScreen() {
    //设置允许Window在危险区域绘制
    val layoutParams = attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        layoutParams.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    attributes = layoutParams
    WindowCompat.setDecorFitsSystemWindows(this, false)
    statusBarColor = Color.TRANSPARENT
}

fun CenterPopupView.showDialog(context: Context) {
    XPopup.Builder(context).dismissOnTouchOutside(false).dismissOnBackPressed(false)
        .hasShadowBg(true).asCustom(this).show()
}


fun showLoadingDialog() {
    LoadingDialog.instance.show()
}

fun dismissLoadingDialog() {
    LoadingDialog.instance.dismiss()
}

fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    ToastUtils.showToast(this, duration)
}

inline fun <reified T : CenterPopupView> BaseActivity.showCenterDialog(
    dialog: T,
    canTouchCancel: Boolean = false,
    canBackCancel: Boolean = false
) {
    XPopup.Builder(this)
        .dismissOnTouchOutside(canTouchCancel)
        .dismissOnBackPressed(canBackCancel)
        .hasShadowBg(true)
        .isDestroyOnDismiss(true)
        .asCustom(dialog)
        .show()
}

fun View.isShowInWindow(context: Context): Boolean {
    val sH = context.resources.displayMetrics.heightPixels
    val sW = context.resources.displayMetrics.widthPixels
    val location = IntArray(2)
    getLocationInWindow(location)
    if (location[0] < sW && location[1] < sH) {
        //在屏幕中是可见的
        return true
    }
    //在屏幕中不可见
    return false
}

fun LayoutCommentBarBinding.setListeners(
    onComment: ((content: String) -> Unit)? = null,
    seeAll: (() -> Unit)? = null,
    focus: (() -> Unit)? = null,
    share: (() -> Unit)? = null
) {
    commentEdt.setOnEditorActionListener { _, actionId, _ ->
        MyApplication.getLastActivity().checkIsLogin {
            (actionId == EditorInfo.IME_ACTION_SEND).trueLet {
                val content = commentEdt.text.toString().trim()
                content.isEmpty().trueLet {
                    "请输入评论内容".toast()
                }.elseLet {
                    showLoadingDialog()
                    //触发回调
                    onComment?.invoke(content)

                }
            }
        }

        return@setOnEditorActionListener false
    }
    commentCountLayout.setOnclick { seeAll?.invoke() }
    btnFocus.setOnclick { MyApplication.getLastActivity().checkIsLogin { focus?.invoke() } }
    btnShare.setOnclick {
        share?.invoke()
//        MyApplication.getLastActivity().checkIsLogin {  }
    }
}

/**********************列表刷新加载控制器**************************/

//刷新组件和绑定的控制器Map（同一时间可能有多个列表）
private val refreshBindMap = HashMap<SmartRefreshLayout, RefreshController>()

/**通过是否可以下拉刷新的标识[refreshFlag]和用于获取数据的回调[callback]创建并绑定控制器*/
fun SmartRefreshLayout.bindController(refreshFlag: Boolean, callback: (page: Int) -> Unit) {
    refreshBindMap[this] = RefreshController(this, refreshFlag, callback)
}

/**获取刷新组件对应的控制器*/
fun SmartRefreshLayout.getController(): RefreshController? {
    return refreshBindMap[this]
}

/**解绑控制器*/
fun SmartRefreshLayout.unBindController() {
    if (refreshBindMap.containsKey(this)) {
        refreshBindMap.remove(this)
    }
}

/**使用标识[flag]设置是否能够上拉加载更多*/
fun SmartRefreshLayout.setCanLoadMore(flag: Boolean) {
    finishRefresh()
    finishLoadMore()
    refreshBindMap[this]?.setEnableLoadMore(flag)
}

fun share(
    activity: BaseActivity,
    data: ShareData,
    scroll: NestedScrollView?= null,
    activeSavePic: Boolean = true
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        PermissionX.init(activity).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    callShare(activity, data, scroll, activeSavePic)
                }
            }
    } else {
        PermissionX.init(activity).permissions(Manifest.permission.READ_MEDIA_IMAGES)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    callShare(activity, data, scroll, activeSavePic)
                }
            }
    }
}

private fun Bitmap.saveImageToGallery(context: Context, path: String) {
    Log.e("来了几次","saveImageToGallery")
    // 首先保存图片
    val file = File(path)
    val appDir = file.parentFile
    if (appDir?.exists() != true) {
        appDir?.mkdir()
    }
    val fileName = file.name
    try {
        val fos = FileOutputStream(file)
        compress(CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    // 其次把文件插入到系统图库
    try {
        MediaStore.Images.Media.insertImage(
            context.contentResolver,
            file.absolutePath, fileName, null
        )
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    // 最后通知图库更新
    context.sendBroadcast(
        Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            Uri.parse("file://$path")
        )
    )
    Log.e("来了几次","通知")
}

fun callShare(activity: BaseActivity, data: ShareData, scroll: NestedScrollView? = null, activeSavePic: Boolean) {
    if (data.isMine || scroll == null) {
        activity.showShareDialog(data, activeSavePic)
    } else {
        var curPos = 0
        if (scroll.scrollY > 0) {
            curPos = scroll.scrollY
            scroll.fullScroll(FOCUS_UP)
        }
        val finalTop = if (activity is BrokerContentActivity) {
            scroll.top
        } else {
            scroll.top + ceil(25 * activity.resources.displayMetrics.density)
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (activity.picShare.isEmpty()) {
                activity.picShare = saveTemp(activity.saveImageCache(finalTop.toInt()))
            }
            data.tempPath = activity.picShare
            data.percentRemain = scroll.getPercentRemain()
            activity.showShareDialog(data, activeSavePic)
        }

        if (curPos > 0) {
            scroll.scrollTo(0, curPos)
        }
    }
}

fun NestedScrollView.getPercentRemain(): String {
    var totalHeight = 0
    // 获取scrollview实际高度
    for (i in 0 until childCount) {
        totalHeight += getChildAt(i).height
    }
    val currentHeight = resources.displayMetrics.heightPixels - top
    val percent = (totalHeight - currentHeight) * 100 / totalHeight
    return percent.toString()
}

fun BaseActivity.screenShot(): Bitmap {
    val view: View = window.decorView
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache()
    return Bitmap.createBitmap(view.drawingCache)
}

fun BaseActivity.saveImageCache(top: Int = resources.getDimensionPixelOffset(R.dimen.dp_50)): Bitmap {
    val bitmap = screenShot()
    // 创建对应大小的bitmap
    return Bitmap.createBitmap(bitmap, 0, top, bitmap.width, bitmap.height - top)
}

fun saveTemp(bm: Bitmap): String {
    //指定我们想要存储文件的地址
    val name = "temp${System.currentTimeMillis()}.jpg"
    saveBitmap(bm, BASE_IMAGE_SAVE_PATH, name)
    return "$BASE_IMAGE_SAVE_PATH$name"
}

fun saveBitmap(bm: Bitmap, targetPath: String, name: String) {
    Log.e("来了几次","saveBitmap")
    val pathFile = File(targetPath)
    val file = File("${targetPath}${name}")
    //判断指定文件夹的路径是否存在
    if (!pathFile.exists()) {
        pathFile.mkdirs()
    }
    bm.saveImageToGallery(MyApplication.getLastActivity(), file.absolutePath)
}