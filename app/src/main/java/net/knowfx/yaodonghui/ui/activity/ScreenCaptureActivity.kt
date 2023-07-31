package net.knowfx.yaodonghui.ui.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityScreenCaptureBinding
import net.knowfx.yaodonghui.ext.BASE_IMAGE_SAVE_PATH
import net.knowfx.yaodonghui.ext.URL_APP_DOWNLOAD
import net.knowfx.yaodonghui.ext.createQRCode
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.saveBitmap
import net.knowfx.yaodonghui.ext.saveImageCache
import net.knowfx.yaodonghui.ext.toast
import kotlin.math.max

class ScreenCaptureActivity : BaseActivity() {
    private val mBinding = lazy { ActivityScreenCaptureBinding.inflate(layoutInflater) }
    override fun getContentView() = mBinding.value.root

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        val path = bundle?.getString("file") ?: ""
        val percentRemain = bundle?.getString("percent") ?: "80"
        if (path.isNotEmpty()) {
            mBinding.value.imgCenter.setImageBitmap(BitmapFactory.decodeFile(path))
            mBinding.value.qrCode.createQRCode(URL_APP_DOWNLOAD)
            mBinding.value.tvPercent.text = "查看剩余${max(0, percentRemain.toInt())}%内容"
            mBinding.value.root.postDelayed({
                val fileName = "share_${System.currentTimeMillis()}.jpg"
                CoroutineScope(Dispatchers.Main).launch {
                    saveBitmap(
                        saveImageCache(0),
                        BASE_IMAGE_SAVE_PATH,
                        fileName
                    )
                    "图片保存在：$BASE_IMAGE_SAVE_PATH$fileName".toast(Toast.LENGTH_LONG)
                }
            }, 300)
        } else {
            "分享出错，请稍后重试".toast()
            finish()
        }
    }
}