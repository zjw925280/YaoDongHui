package net.knowfx.yaodonghui.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityArticleCommentContentBinding
import net.knowfx.yaodonghui.entities.MyCommentContentData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.getByteArray
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.toast

class ArticleCommentContentActivity : BaseActivity() {
    private lateinit var mBinding: ActivityArticleCommentContentBinding
    private var mTargetId = 0
    private var mModel = ""
    override fun getContentView(): View {
        mBinding = ActivityArticleCommentContentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        commonViewModel.value.commentContent.observe(this) {
            it?.apply {
                result(MyCommentContentData(), error = {}, success = { data ->
                    mBinding.apply {
                        tvTime.text = data.createTime.getCreateFormatTime()
                        tvContent.text = data.content
                        layoutArticle.apply {
                            val height = resources.getDimensionPixelOffset(R.dimen.dp_40)
                            val corner = resources.getDimensionPixelOffset(R.dimen.dp_5)
                            thumbIv.intoLogoOrCover(data.coverPicture, height, corner)
                            val labelStr = getTextFromModel(data.model)
                            val blanks = when (labelStr.length) {
                                3 -> "\t\t\t\t\t\t\t"
                                4 -> "\t\t\t\t\t\t\t\t"
                                5 -> "\t\t\t\t\t\t\t\t\t"
                                else -> "\t\t\t"
                            }
                            labelTv.text = getTextFromModel(data.model)
                            titleTv.text = getString(
                                R.string.string_text_supervise_news_title_pattern,
                                blanks,
                                data.title
                            )
                            nameTimeTv.text = getString(
                                R.string.string_text_supervise_news_name_time,
                                getUserData()?.nickname ?: "",
                                data.createTime.getCreateFormatTime()
                            )

                            root.setOnclick {
                                jumpFromPush(data.model, data.dealerId)
                            }
                        }
                    }
                })
            }
        }
        commonViewModel.value.commentDelResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    "删除成功".toast()
                    val intent = Intent()
                    intent.action = "COMMENT_DEL"
                    intent.putExtra("id", mTargetId)
                    sendBroadcast(intent)
                    finish()
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mTargetId = bundle?.getInt("id") ?: 0
        mModel = bundle?.getString("model") ?: ""
        if (mTargetId == 0 || mModel.isEmpty()) {
            "评论不存在，请稍后重试".toast()
            finish()
        }
        commonViewModel.value.getMyCommentContent(mTargetId)
        mBinding.layoutArticle.apply {
            labelTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelOffset(R.dimen.sp_10).toFloat())
            titleTv.setTextColor(
                ContextCompat.getColor(
                    this@ArticleCommentContentActivity,
                    R.color.color_999999
                )
            )
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelOffset(R.dimen.sp_14).toFloat())
            titleTv.maxLines = 1
            titleTv.ellipsize = TextUtils.TruncateAt.END
            nameTimeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelOffset(R.dimen.sp_12).toFloat())
        }
        mBinding.btnBack.setOnclick { finish() }
        mBinding.btnDelete.setOnclick {
            commonViewModel.value.delComment(
                mTargetId.toString(),
                mModel
            )
        }
    }
}