package net.knowfx.yaodonghui.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityWebBinding
import net.knowfx.yaodonghui.ext.setOnclick

class WebActivity : BaseActivity() {
    private val mBinding = lazy { ActivityWebBinding.inflate(layoutInflater) }
    override fun getContentView() = mBinding.value.root

    @SuppressLint("SetJavaScriptEnabled")
    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        val url = bundle?.getString("url") ?: ""
        val title = bundle?.getString("title") ?: ""
        mBinding.value.windowTitle.text = title
        if (url.isNotEmpty()) {
            mBinding.value.webContent.apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }
                settings.apply {
                    javaScriptEnabled = true
                    loadUrl(url)
                }
            }
        }
        mBinding.value.btnBack.setOnclick { finish() }
    }
}