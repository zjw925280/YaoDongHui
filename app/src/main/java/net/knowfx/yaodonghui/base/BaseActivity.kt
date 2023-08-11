package net.knowfx.yaodonghui.base

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.KEY_TOKEN
import net.knowfx.yaodonghui.ext.KEY_USER_INFO
import net.knowfx.yaodonghui.ext.clearAllMMKV
import net.knowfx.yaodonghui.ext.clearCacheKeys
import net.knowfx.yaodonghui.ext.delSingleData
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.fullScreen
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.totalFullScreen
import net.knowfx.yaodonghui.net.NetState
import net.knowfx.yaodonghui.net.NetworkStateManager
import net.knowfx.yaodonghui.ui.activity.MainActivity
import net.knowfx.yaodonghui.ui.dialogs.DialogShare
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.CommonViewModel
import java.io.File

abstract class BaseActivity : AppCompatActivity() {


    /**
     * 跳转传递的数据集合对象
     */
    var bundle: Bundle? = null

    /**
     * 应用是否处于前台
     */
    var mIsAppForeground = false
    var picShare = ""

    protected val commonViewModel = lazy { ViewModelProvider(this)[CommonViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        MyApplication.addActivity(this)
        bundle = intent.extras
        //弹框状态栏显示
        super.onCreate(savedInstanceState)
        //添加当前界面上下文对象
        setStatusColorAndText()
        setContentView(getContentView())
        initViewModel()
        setData(savedInstanceState)
        NetworkStateManager.instance.mNetworkStateCallback.observe(this) {
            onNetworkStateChanged(it)
        }
    }

    abstract fun getContentView(): View

    open fun initViewModel() {
        commonViewModel.value.logoutResult.observe(this) {
            clearLoginUser()
            "退出登录成功".toast()
            if (this !is MainActivity) {
                jumpToTarget(this, MainActivity::class.java)
            }
        }

        commonViewModel.value.delAccountResult.observe(this) {
            it?.apply {
                result(String(), error = {
                    "注销账号失败，请稍后重试".toast()
                }, success = {
                    "注销账号成功，感谢您的使用".toast()
                    clearAllMMKV()
                    backToLogin()
                })
            }
        }
    }

    private fun setStatusColorAndText() {
        //设置状态栏文字颜色, 隐藏底部导航栏
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = isUseBlackText()
        window.navigationBarColor = Color.WHITE
        if (isUseTotalFullScreenMode()) {
            window.totalFullScreen(controller)
        } else if (isUseFullScreenMode()) {
            window.fullScreen()
            if (getStatusBarColor() != R.color.white) {
                window.statusBarColor =
                    ContextCompat.getColor(this@BaseActivity, getStatusBarColor())
            }
        } else {
            window.statusBarColor = ContextCompat.getColor(this@BaseActivity, getStatusBarColor())
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

    open fun getStatusBarColor() = R.color.white

    open fun isUseFullScreenMode() = false

    open fun isUseBlackText() = true

    open fun isUseTotalFullScreenMode() = false

    override fun onResume() {
        super.onResume()
        resume()
        if (!mIsAppForeground) {
            mIsAppForeground = true
        }
    }

    open fun resume() {}
    open fun stop() {}

    override fun onStop() {
        super.onStop()
        stop()
        mIsAppForeground = isAppOnForeground()
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (picShare.isNotEmpty()) {
            val file = File(picShare)
            file.deleteOnExit()
            picShare = ""
        }
        dismissLoadingDialog()
        MyApplication.deleteActivity(this)
    }

    /**
     * 数据加载
     */
    open fun setData(savedInstanceState: Bundle?) {}

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(it: NetState) {}

    /**
     * 程序是否在前台运行
     */
    private fun isAppOnForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // 获取Android设备中所有正在运行的App
        val appProcesses = activityManager.runningAppProcesses
        appProcesses.forEach {
            if (it.processName.equals(packageName)
                && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return true
            }
        }
        return false
    }

    private lateinit var mShareDialog: DialogShare

    open fun showShareDialog(data: ShareData, activeSavePic: Boolean) {
        if (data.tempPath.isNotEmpty() || data.thumbBitmap != null) {
            this::mShareDialog.isInitialized.falseLet {
                mShareDialog = DialogShare(activeSavePic)
            }
            mShareDialog.show(supportFragmentManager, data, this.localClassName)
        } else {
            "分享失败，请稍后重试".toast()
        }
    }

    open fun showToast(content: String) {
        content.toast()
    }

    protected fun logout() {
        commonViewModel.value.logout()
    }

    protected fun clearLoginUser() {
        delSingleData(KEY_TOKEN)
        delSingleData(KEY_USER_INFO)
        clearCacheKeys()
    }

    private fun backToLogin() {
        jumpToTarget(this, MainActivity::class.java)
    }

    protected fun delAccount() {
        commonViewModel.value.delAccount()
    }
}