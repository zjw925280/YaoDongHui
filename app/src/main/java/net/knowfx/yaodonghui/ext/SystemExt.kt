package net.knowfx.yaodonghui.ext

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import cn.jpush.android.api.CustomMessage
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.IndexFunctionListData
import net.knowfx.yaodonghui.entities.UserInfoData
import net.knowfx.yaodonghui.ui.activity.ArticleContentActivity
import net.knowfx.yaodonghui.ui.activity.ArticleListActivity
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.ui.activity.ClassContentActivity
import net.knowfx.yaodonghui.ui.activity.ClassListActivity
import net.knowfx.yaodonghui.ui.activity.DrawContentActivity
import net.knowfx.yaodonghui.ui.activity.DrawListActivity
import net.knowfx.yaodonghui.ui.activity.FunctionAllActivity
import net.knowfx.yaodonghui.ui.activity.LoginActivity
import net.knowfx.yaodonghui.ui.activity.WatchBrokerActivity
import net.knowfx.yaodonghui.ui.activity.WatchExploreActivity
import net.knowfx.yaodonghui.utils.MyApplication
import org.json.JSONObject
import java.io.Serializable

fun <T : BaseActivity> jumpToTarget(
    startAct: Context, target: Class<T>, params: HashMap<String, Any> = HashMap()
) {
    val intent = Intent(startAct, target)
    params.isNotEmpty().trueLet {
        val bundle = Bundle()
        params.forEach {
            putParam(bundle, it.key, it.value)
        }
        intent.putExtras(bundle)
    }
    startAct.startActivity(intent)
}

fun FragmentActivity.registerLauncher(
    activityResult: (result: ActivityResult) -> Unit
): ActivityResultLauncher<Intent> {
    val activityResultContract = object : ActivityResultContract<Intent, ActivityResult>() {
        override fun createIntent(context: Context, input: Intent): Intent = input
        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
            ActivityResult(resultCode, intent)
    }
    val mActivityResultCallback = ActivityResultCallback<ActivityResult> { result ->
        activityResult.invoke(result)
    }


    return registerForActivityResult(activityResultContract, mActivityResultCallback)
}

fun FragmentActivity.jumpFromPush(model: String, id: Int) {
    when (model) {
        "ZXH", "HQST", "DSPH", "ZXZX", "JYSZX" -> {
            //真相汇,汇圈神探,毒舌评汇,最新资讯,交易商资讯
            jumpToTarget(
                this,
                ArticleContentActivity::class.java,
                hashMapOf(Pair("id", id), Pair("flag", model))
            )
        }

        "HHQ" -> {
            //"画汇圈"
            jumpToTarget(
                this,
                DrawContentActivity::class.java,
                hashMapOf(Pair("id", id), Pair("code", model))
            )
        }

        "JGXT" -> {//"经哥学堂"
            jumpToTarget(
                this,
                ClassContentActivity::class.java,
                hashMapOf(Pair("id", id))
            )
        }

        "JYS" -> {//"交易商"
            jumpToTarget(
                this,
                BrokerContentActivity::class.java,
                hashMapOf(Pair("brokerId", id))
            )

        }

        else -> {}
    }//"未知"
}

fun <T : BaseActivity> jumpToTargetForResult(
    startAct: FragmentActivity,
    target: Class<T>,
    launcher: ActivityResultLauncher<Intent>,
    params: HashMap<String, Any> = HashMap(),
) {
    val intent = Intent(startAct, target)
    params.isNotEmpty().trueLet {
        val bundle = Bundle()
        params.forEach {
            putParam(bundle, it.key, it.value)
        }
        intent.putExtras(bundle)
    }
    launcher.launch(intent)
}

fun UserInfoData.saveUserData() {
    saveData(KEY_USER_INFO, Gson().toJson(this))
}

fun getUserData(): UserInfoData? {
    return if (hasDataKey(KEY_USER_INFO)) {
        val infoStr = readData(KEY_USER_INFO, "")
        return Gson().fromJson(infoStr, UserInfoData::class.java)
    } else {
        null
    }
}

fun AppCompatTextView.startCountDownForGetCode() {
    object : CountDownTimer(60 * 1000L, 1000L) {
        override fun onTick(remaind: Long) {
            text = context.getString(R.string.string_phone_code_count, (remaind / 1000) + 1)
        }

        override fun onFinish() {
            text = context.getString(R.string.string_phone_code_get)
            isEnabled = true
        }

        fun startCount() {
            isEnabled = false
            text = context.getString(R.string.string_phone_code_count, 60)
            start()
        }
    }.startCount()
}

fun String.saveToken() {
    saveData(KEY_TOKEN, this)
}

fun getToken(): String {
    return readData(KEY_TOKEN, "")
}


private fun putParam(bundle: Bundle, key: String, value: Any) {
    when (value) {
        is Float -> {
            bundle.putFloat(key, value)
        }

        is Int -> {
            bundle.putInt(key, value)
        }

        is String -> {
            bundle.putString(key, value)
        }

        is Boolean -> {
            bundle.putBoolean(key, value)
        }

        is Serializable -> {
            bundle.putSerializable(key, value)
        }

        is Parcelable -> {
            bundle.putParcelable(key, value)
        }

        else -> {
        }
    }
}

fun BaseActivity.indexFunctionJump(
    data: IndexFunctionListData.IndexFunctionData
) {
    val map = hashMapOf<String, Any>(Pair("code", data.code))
    when (data.code) {
        "BG" -> {//曝光
            jumpToTarget(this, WatchExploreActivity::class.java)
        }

        "JGPH" -> {//监管排行
            jumpToTarget(
                this,
                WatchBrokerActivity::class.java,
                hashMapOf(Pair("index", 0))
            )
        }

        "PPPH" -> {//品牌排行
            jumpToTarget(
                this,
                WatchBrokerActivity::class.java,
                hashMapOf(Pair("index", 1))
            )

        }

        "HPT" -> {//黑平台
            jumpToTarget(
                this,
                WatchBrokerActivity::class.java,
                hashMapOf(Pair("index", 2))
            )

        }

        "JGXT" -> {
            //经哥学堂
            jumpToTarget(this, ClassListActivity::class.java, map)
        }

        "HHQ" -> {
            //画汇圈
            jumpToTarget(this, DrawListActivity::class.java, map)
        }

        "QB" -> {
            jumpToTarget(this, FunctionAllActivity::class.java)
        }

        "ZXH", "HQST", "DSPH" -> {//真相汇，汇圈神探，毒蛇评汇
            jumpToTarget(this, ArticleListActivity::class.java, map)
        }

        else -> {}
    }
}


fun FragmentActivity.checkIsLogin(
    isForResult: Boolean = false,
    launcher: ActivityResultLauncher<Intent>? = null,
    action: () -> Unit
) {
    if (getToken().isEmpty() || getUserData() == null) {
        if (isForResult) {
            launcher?.apply {
                jumpToTargetForResult(this@checkIsLogin, LoginActivity::class.java, this)
            } ?: {
                throw NotLoginHasNotImpException("如果需要登录界面的跳转回执，则需要传递注册的launcher")
            }
        } else {
            jumpToTarget(this, LoginActivity::class.java)
        }
    } else {
        action.invoke()
    }
}

private class NotLoginHasNotImpException(override val message: String = "") : Throwable()

/**************通知相关******************/

fun showNotification(customMessage: CustomMessage) {
    val params = customMessage.extra
    val jsonObject = JSONObject(JSONObject(params).optString("value"))
    val manager = MyApplication.getInstance()
        .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val builder = NotificationCompat.Builder(
        MyApplication.getInstance(),
        MyApplication.getInstance().packageName
    )
    val notification = builder.setContentTitle(customMessage.title)//标题
        .setContentText(customMessage.message)//内容
        .setSmallIcon(R.mipmap.ic_launcher)//小图标
        .setLargeIcon(
            BitmapFactory.decodeResource(
                MyApplication.getInstance().resources,
                R.mipmap.ic_launcher
            )
        )//大图标
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)//优先级（默认）
        .setAutoCancel(true)
        .setContentIntent(
            getPendingIntent(
                jsonObject.optString("model"),
                jsonObject.optInt("buinessId")
            )
        )//设置跳转
        .build()
    manager.notify(0, notification)
}


fun getPendingIntent(model: String, id: Int): PendingIntent {
    val intent = Intent()
    val bundle = Bundle()
    when (model) {
        "ZXH", "HQST", "DSPH", "ZXZX", "JYSZX" -> {
            //真相汇,汇圈神探,毒舌评汇,最新资讯,交易商资讯
            intent.setClass(MyApplication.getLastActivity(), ArticleContentActivity::class.java)
            putParam(bundle, "id", id)
            putParam(bundle, "flag", model)
        }

        "HHQ" -> {
            //"画汇圈"
            intent.setClass(MyApplication.getLastActivity(), DrawContentActivity::class.java)
            putParam(bundle, "id", id)
            putParam(bundle, "code", model)
        }

        "JGXT" -> {//"经哥学堂"
            intent.setClass(MyApplication.getLastActivity(), ClassContentActivity::class.java)
            putParam(bundle, "id", id)
        }

        "JYS" -> {//"交易商"
            intent.setClass(MyApplication.getLastActivity(), BrokerContentActivity::class.java)
            putParam(bundle, "brokerId", id)

        }

        else -> {}//"未知"
    }
    intent.putExtras(bundle)
    return PendingIntent.getActivity(MyApplication.getLastActivity(), 0, intent, FLAG_IMMUTABLE)
}