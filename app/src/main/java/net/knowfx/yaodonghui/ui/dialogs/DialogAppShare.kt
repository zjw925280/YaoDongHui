package net.knowfx.yaodonghui.ui.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.DialogAppShareBinding
import net.knowfx.yaodonghui.ext.BASE_IMAGE_SAVE_PATH
import net.knowfx.yaodonghui.ext.URL_APP_DOWNLOAD
import net.knowfx.yaodonghui.ext.createQRCode
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.saveBitmap
import net.knowfx.yaodonghui.ext.toast

class DialogAppShare(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId() = R.layout.dialog_app_share
    override fun onCreate() {
        super.onCreate()
        val viewBinding = DialogAppShareBinding.bind(centerPopupContainer.getChildAt(0))
        val size = resources.getDimensionPixelSize(R.dimen.dp_200)
        viewBinding.qrCode.createQRCode(URL_APP_DOWNLOAD)
        postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                val fileName = "share_${System.currentTimeMillis()}.jpg"
                saveBitmap(
                    getBitmap(),
                    BASE_IMAGE_SAVE_PATH,
                    fileName
                )
                "图片保存在：$BASE_IMAGE_SAVE_PATH$fileName".toast(Toast.LENGTH_LONG)
            }
        }, 500)
    }

    private fun getBitmap(): Bitmap {
        measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        // 创建对应大小的bitmap
        val bitmap: Bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}