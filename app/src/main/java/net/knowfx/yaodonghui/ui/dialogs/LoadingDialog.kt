package net.knowfx.yaodonghui.ui.dialogs

import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import net.knowfx.yaodonghui.utils.MyApplication

class LoadingDialog {

    companion object {
        val instance: LoadingDialog by lazy { LoadingDialog() }
    }

    private var loading: LoadingPopupView? = null
    private var mIsShowing = false

    /**
     * 在fragment里面调用这个
     *  注意 先不要传hint  可能宽度不够（带验证码）
     */
    fun show(hint: String = "") {
        if (!mIsShowing) {
            mIsShowing = true
            dialog(hint)
            loading?.run { if (!isShow) show() }
        }
    }

    fun dismiss() {
        if (mIsShowing) {
            mIsShowing = false
            loading?.run { dismiss() }
            loading = null
        }
    }

    private fun dialog(hint: String = "") {
        loading = XPopup.Builder(MyApplication.getLastActivity())
            .dismissOnTouchOutside(false)//点击外部是否取消
            .dismissOnBackPressed(false)//按返回是否取消
            .isDestroyOnDismiss(true) //如果弹框多次调用就设置为false
            .isRequestFocus(false)
            .asLoading(hint) as LoadingPopupView
    }
}