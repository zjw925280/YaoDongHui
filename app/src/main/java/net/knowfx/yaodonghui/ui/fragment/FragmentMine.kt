package net.knowfx.yaodonghui.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.FragmentMineBinding
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.entities.UserInfoData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.AboutActivity
import net.knowfx.yaodonghui.ui.activity.BusinessWorkActivity
import net.knowfx.yaodonghui.ui.activity.FeedbackActivity
import net.knowfx.yaodonghui.ui.activity.MainActivity
import net.knowfx.yaodonghui.ui.activity.MyFocusActivity
import net.knowfx.yaodonghui.ui.activity.MyPostHistoryActivity
import net.knowfx.yaodonghui.ui.activity.MyWatchHistoryActivity
import net.knowfx.yaodonghui.ui.activity.PushHistoryActivity
import net.knowfx.yaodonghui.ui.activity.SettingActivity

class FragmentMine : Fragment() {
    private lateinit var mViewBinding: FragmentMineBinding
    private var mUserData: UserInfoData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentMineBinding.inflate(inflater)
        setMultipleClick(
            mViewBinding.mineBtnSetting,
            mViewBinding.mineBtnPost,
            mViewBinding.mineBtnFocus,
            mViewBinding.mineBtnLookHistory,
            mViewBinding.mineBtnCom,
            mViewBinding.mineBtnShare,
            mViewBinding.mineBtnPushHistory,
            mViewBinding.mineLabelFeedback,
            mViewBinding.mineBtnLogin,
            mViewBinding.mineBtnRegister,
            mViewBinding.mineLabelAbout
        ) {
            when (it) {
                mViewBinding.mineBtnSetting -> {
                    //点击设置
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(this, SettingActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnPost -> {
                    //点击我的发布
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(this, MyPostHistoryActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnFocus -> {
                    //点击我的关注
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(true, loginLauncher) {
                            jumpToTarget(this, MyFocusActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnLookHistory -> {
                    //点击浏览历史
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(this, MyWatchHistoryActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnCom -> {
                    //点击商务合作
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(this, BusinessWorkActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnShare -> {
                    //点击分享
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            val data = ShareData(
                                title = "要懂匯",
                                content = getString(R.string.string_broker_share_content),
                                url = hashMapOf<String, Any>(Pair("type", 0)).createShareUrl(""),
                                thumbBitmap = Bitmap.createScaledBitmap(
                                    BitmapFactory.decodeResource(
                                        resources,
                                        R.drawable.icon_share_comment
                                    ), 80, 80, true
                                ),
                                isMine = true
                            )
                            share(requireActivity() as BaseActivity, data)
                        }
                    }
                }

                mViewBinding.mineBtnPushHistory -> {
                    //点击推送历史
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(
                                this,
                                PushHistoryActivity::class.java,
                                hashMapOf(Pair("title", "推送历史"))
                            )
                        }
                    }
                }

                mViewBinding.mineLabelFeedback -> {
                    //点击意见反馈
                    (requireActivity() as MainActivity).apply {
                        checkIsLogin(isForResult = true, launcher = loginLauncher) {
                            jumpToTarget(this, FeedbackActivity::class.java)
                        }
                    }
                }

                mViewBinding.mineBtnLogin -> {
                    //点击登录按钮
                    (requireActivity() as MainActivity).jumpToLoginActivity()
                }

                mViewBinding.mineBtnRegister -> {
                    //点击注册按钮
                    (requireActivity() as MainActivity).jumpToRegisterActivity()
                }

                else -> {
                    //点击关于
                    jumpToTarget(requireActivity() as BaseActivity, AboutActivity::class.java)
                }
            }
        }
        refreshData()
        return mViewBinding.root
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    fun refreshData() {
        mUserData = getUserData()
        mUserData?.apply {
            mViewBinding.mineLayoutLogin.gone()
            mViewBinding.mineLayoutInfo.visible()
            mViewBinding.mineAvatar.intoCircle(userhead, R.mipmap.ic_avatar_default)
            mViewBinding.mineTextNickname.text = nickname.ifEmpty { "用户$userId" }
            mViewBinding.mineTextId.text = getString(R.string.string_user_id, userId)
        } ?: apply {
            mViewBinding.mineAvatar.intoCircle(R.mipmap.ic_avatar_default)
            mViewBinding.mineLayoutInfo.gone()
            mViewBinding.mineLayoutLogin.visible()
        }
    }
}