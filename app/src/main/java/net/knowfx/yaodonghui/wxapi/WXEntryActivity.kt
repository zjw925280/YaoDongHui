package net.knowfx.yaodonghui.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import net.knowfx.yaodonghui.utils.LogUtils
import net.knowfx.yaodonghui.utils.ToastUtils

class WXEntryActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val api = WXAPIFactory.createWXAPI(this, "wx94da845a0957886d", true)
        api.handleIntent(intent, object : IWXAPIEventHandler {
            override fun onReq(baseReq: BaseReq?) {
                // 处理微信请求
            }

            override fun onResp(baseResp: BaseResp?) {
                // 处理微信响应，例如分享成功或失败的回调
                Log.e("回调了","回调了")
            }
        })
        finish()
    }
}