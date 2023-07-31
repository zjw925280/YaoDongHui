package net.knowfx.yaodonghui.utils

import android.content.Context
import cn.jpush.android.api.CmdMessage
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.ext.showNotification
import net.knowfx.yaodonghui.ui.activity.WebActivity
import org.json.JSONObject
import kotlin.random.Random

class JPushReceiver : JPushMessageReceiver() {
    override fun onNotifyMessageOpened(context: Context, notificationMessage: NotificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage)
        val params = notificationMessage.notificationExtras
        "notification params ======= $params".logE("ralph")
        val json = JSONObject(JSONObject(params).optString("value"))
        val url = json.optString("url")
        val model = json.optString("model")
        val id = json.optInt("buinessId", 0)
        if (url.isNotEmpty()) {
            jumpToTarget(
                MyApplication.getLastActivity(),
                WebActivity::class.java,
                hashMapOf(Pair("url", url), Pair("title", "推送"))
            )
        } else if (model.isNotEmpty() && id > 0) {
            MyApplication.getLastActivity().jumpFromPush(model, id)
        }
    }


    override fun onCommandResult(context: Context?, cmdMessage: CmdMessage?) {
        super.onCommandResult(context, cmdMessage)
        // 注册失败 + 三方厂商注册回调
        "[onCommandResult]$cmdMessage".logE("JIGUANG")
        cmdMessage?.apply {
            if (cmdMessage.errorCode == 2008) {
                //注册成功
                JPushInterface.setAlias(
                    MyApplication.getLastActivity(),
                    Random(1000).nextInt(),
                    getUserData()?.phone ?: ""
                )
            }
        }
    }
}