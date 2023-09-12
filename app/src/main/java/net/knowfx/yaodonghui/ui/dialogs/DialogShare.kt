package net.knowfx.yaodonghui.ui.dialogs

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
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
import net.knowfx.yaodonghui.utils.ToastUtils
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
            mBinding.layoutQQ,
            mBinding.layoutCircle,
            mBinding.layoutCopy,
            mBinding.layoutWechat,
            mBinding.layoutSavePic
        ) {
            when (it) {
                mBinding.layoutQQ -> {
                    //QQ分享啦
                    shareTextToQQ()
                }
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
        Log.e("mShareData","mShareData数据=="+Gson().toJson(mShareData));
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


        fun shareTextToQQ() {
            Tencent.setIsPermissionGranted(true);
            Log.e("mShareData","mShareData数据=="+Gson().toJson(mShareData));
            val tencent: Tencent = Tencent.createInstance("102067312", context)
            val params = Bundle()
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareData.title)
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareData.content)
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareData.url) //分享链接

            if (mShareData.picArray.isNotEmpty()) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareData.picArray.toString()) //分享的图片链接
            } else {
                val toJson = Gson().toJson(mShareData.thumbBitmap)
                val toByteArray = toJson.toByteArray(Charsets.UTF_8)

                Log.e("图片","thumb.url="+toByteArray);
                params.putByteArray(QQShare.SHARE_TO_QQ_IMAGE_URL,toByteArray) //分享的图片链接
            }
            tencent.shareToQQ(activity, params, object : IUiListener {
                override fun onComplete(p0: Any?) {
                    // 分享成功
                    ToastUtils.showToast("分享成功")
                    dismiss()
                }

                override fun onError(p0: UiError?) {
                    // 分享失败
                    ToastUtils.showToast("分享失败")

                }

                override fun onCancel() {
                    // 分享取消
                    ToastUtils.showToast("分享取消")
                }

                override fun onWarning(p0: Int) {
                  Log.e("为啥子来这","为啥子来这");
                }

            })

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