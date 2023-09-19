package net.knowfx.yaodonghui.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import com.tencent.mmkv.MMKV
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivitySettingBinding
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.UserInfoData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.clearApplicationCache
import net.knowfx.yaodonghui.ext.clearCacheKeys
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getApplicationCacheSize
import net.knowfx.yaodonghui.ext.getMMKVSize
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.registerLauncher
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveUserData
import net.knowfx.yaodonghui.ext.showCenterDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ui.dialogs.DialogInput
import net.knowfx.yaodonghui.ui.dialogs.DialogRemain
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.imageSelector.SelectImageUtils
import net.knowfx.yaodonghui.viewModels.SettingViewModel

@OptIn(ExperimentalCoilApi::class)
class SettingActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivitySettingBinding
    private val mModel = lazy { ViewModelProvider(this)[SettingViewModel::class.java] }
    private lateinit var mAuthenticationLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        mAuthenticationLauncher = registerLauncher {
            it.data?.apply {
                if (getBooleanExtra("isSuccess", false)) {
                    mViewBinding.labelIdentity.text = "已认证"
                    mViewBinding.labelIdentity.isEnabled = false
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(): View {
        mViewBinding = ActivitySettingBinding.inflate(layoutInflater)
        addListeners()
        mViewBinding.labelClearCacheTv.text=getApplicationCacheSize(this)

        return mViewBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.userInfo.observe(this) {
            dismissLoadingDialog()
            it?.result(UserInfoData(), error = { msg -> msg.toast() }, success = { data ->
                data.saveUserData()
                showUserInfo(data)
            })
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        val userInfo = getUserData()
        userInfo?.apply {
            showUserInfo(this)
        }


    }

    private fun showUserInfo(data: UserInfoData) {
        mViewBinding.btnAvatar.intoCircle(data.userhead, R.mipmap.ic_avatar_default)
        mViewBinding.textNickname.text = data.nickname
        mViewBinding.radioMan.isSelected = data.sex == 1
        mViewBinding.radioWoman.isSelected = data.sex != 1
        if (data.iscert == 1) {
            mViewBinding.labelIdentity.text = "已认证"
            mViewBinding.labelIdentity.isEnabled = false
        }
    }

    private fun addListeners() {
        setMultipleClick(
            mViewBinding.btnAvatar,
            mViewBinding.btnBack,
            mViewBinding.labelNickname,
            mViewBinding.radioMan,
            mViewBinding.radioWoman,
            mViewBinding.labelModifyPwd,
            mViewBinding.relayoutche,
            mViewBinding.btnDestroy,
            mViewBinding.btnLogout,
            mViewBinding.labelIdentity
        ) {
            when (it) {
                mViewBinding.btnAvatar -> {
                    //跳转系统相册选择头像
                    SelectImageUtils.dialogImage(
                        this,
                        true,
                        1,
                        SelectImageUtils.MODE_CAPTURE
                    ) { list ->
                        if (!list.isNullOrEmpty()) {
                            mModel.value.changeAvatar(list[0])
                        }
                    }
                }

                mViewBinding.btnBack -> {
                    //返回上一界面
                    this.finish()
                }

                mViewBinding.labelNickname -> {
                    //修改昵称
                    showCenterDialog(dialog = DialogInput(this, "修改昵称", "请输入昵称") { name ->
                        mModel.value.changeNickname(name)
                    }, canBackCancel = true, canTouchCancel = true)


                }

                mViewBinding.radioMan -> {
                    //选择男性
                    changeSex(true)
                }

                mViewBinding.radioWoman -> {
                    //选择女性
                    changeSex(false)
                }

                mViewBinding.labelModifyPwd -> {
                    //修改密码
                    startActivity(Intent(this, ModifyPwdActivity::class.java))
                }

                mViewBinding.relayoutche -> {

                    //清除缓存
                    clearApplicationCache(this)
                    Coil.imageLoader(MyApplication.getInstance()).diskCache?.clear()
                    "缓存清除成功".toast()
                    mViewBinding.labelClearCacheTv.text="0KB"
                }

                mViewBinding.btnDestroy -> {
                    //注销账号
                    showCenterDialog(
                        DialogRemain(
                            this,
                            title = "系统提示",
                            content = "是否注销账号?",
                            onConfirm = {
                                delAccount()
                            })
                    )
                }

                mViewBinding.btnLogout -> {
                    //退出登录
                    showCenterDialog(
                        DialogRemain(this, content = "是否退出登录?", onConfirm = {
                            logout()
                        })
                    )
                }

                mViewBinding.labelIdentity -> {
                    checkIsLogin {
                        jumpToTargetForResult(
                            this,
                            AuthenticationActivity::class.java,
                            mAuthenticationLauncher
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun changeSex(isMan: Boolean) {
        mViewBinding.radioMan.isSelected = isMan
        mViewBinding.radioWoman.isSelected = !isMan
        mModel.value.changeSex(if (isMan) 1 else 0)
    }
}