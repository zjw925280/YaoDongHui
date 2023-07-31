package net.knowfx.yaodonghui.ui.dialogs

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.DialogShareBinding
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.copyString
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getByteArray
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.showCenterDialog
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.ScreenCaptureActivity
import java.net.URL


class DialogShare(private val isActivePic: Boolean = true) :
    BaseBottomDialog() {
    private lateinit var mBinding: DialogShareBinding
    private lateinit var mShareData: ShareData
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = DialogShareBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.layoutSavePic.apply {
            if (isActivePic) {
                visible()
            } else {
                gone()
            }
        }
        setMultipleClick(
            mBinding.layoutCircle,
            mBinding.layoutCopy,
            mBinding.layoutWechat,
            mBinding.layoutSavePic
        ) {
            when (it) {
                mBinding.layoutWechat -> {
                    //微信分享啦
                    shareToWechatChat()
                }

                mBinding.layoutCircle -> {
                    //朋友圈分享啦
                    shareToWechatCircle()
                }

                mBinding.layoutSavePic -> {
                    //保存图片啦
                    if (mShareData.isMine) {
                        (requireActivity() as BaseActivity).showCenterDialog(
                            DialogAppShare(
                                requireContext()
                            ), canTouchCancel = true, canBackCancel = true
                        )
                    } else {
                        jumpToTarget(
                            context as BaseActivity,
                            ScreenCaptureActivity::class.java,
                            hashMapOf(
                                Pair("file", mShareData.tempPath),
                                Pair("percent", mShareData.percentRemain)
                            )
                        )
                    }
                    dismissAllowingStateLoss()
                }

                else -> {
                    //复制链接啦
                    mShareData.url.copyString(requireContext())
                }
            }
        }
    }

    fun show(manager: FragmentManager, data: ShareData, tag: String = "") {
        mShareData = data
        show(manager, tag)
    }

    private fun shareToWechatChat() {
        showLoadingDialog()
        if (mShareData.picArray.isEmpty() && mShareData.thumbBitmap == null) {
            CoroutineScope(Dispatchers.Main).launch {
                getBitmap(success = {
                    doShare(SendMessageToWX.Req.WXSceneSession)
                    dismissAllowingStateLoss()
                }, fail = {
                    "分享失败，请稍后重试".toast()
                })
            }
        } else {
            doShare(SendMessageToWX.Req.WXSceneSession)
        }
    }

    private fun shareToWechatCircle() {
        showLoadingDialog()
        if (mShareData.picArray.isEmpty() && mShareData.thumbBitmap == null) {
            CoroutineScope(Dispatchers.IO).launch {
                getBitmap(success = {
                    doShare(SendMessageToWX.Req.WXSceneTimeline)
                    dismissAllowingStateLoss()
                }, fail = {
                    "分享失败，请稍后重试".toast()
                })
            }
        } else {
            doShare(SendMessageToWX.Req.WXSceneTimeline)
        }
    }

    private fun doShare(shareType: Int) {
        val api = WXAPIFactory.createWXAPI(requireContext(), BuildConfig.WECHAT_APP_ID)
        val obj = WXWebpageObject()
        obj.webpageUrl = mShareData.url
        val msg = WXMediaMessage(obj)
        msg.title = mShareData.title
        msg.description = mShareData.content
        if (mShareData.picArray.isNotEmpty()) {
            msg.thumbData = mShareData.picArray
        } else {
            msg.setThumbImage(mShareData.thumbBitmap)
        }
        val req = SendMessageToWX.Req()
        req.transaction =
            if (shareType == SendMessageToWX.Req.WXSceneTimeline) "share_to_circle" else "share_to_chat"
        req.message = msg
        req.scene = shareType
        api.sendReq(req)
        dismissLoadingDialog()
        dismiss()
    }

    private suspend fun getBitmap(success: () -> Unit, fail: () -> Unit) {
        val url = URL(mShareData.iconPath)
        val resource = BitmapFactory.decodeStream(withContext(Dispatchers.IO) {
            url.openStream()
        })
        mShareData.picArray = resource.getByteArray()
        withContext(Dispatchers.Main) {
            success.invoke()
        }
    }
}