package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityArticleConentBinding
import net.knowfx.yaodonghui.entities.ArticleContentData
import net.knowfx.yaodonghui.entities.CommentData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.getArticleShareBitmap
import net.knowfx.yaodonghui.ext.getByteArray
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setListeners
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.ArticleViewModel
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class ArticleContentActivity : BaseActivity() {
    private lateinit var mBinding: ActivityArticleConentBinding
    private val mModel = lazy { ViewModelProvider(this)[ArticleViewModel::class.java] }
    private var mTargetId: Int = 0
    private var mFlag: String = ""
    private val mCommentAdapter = CommonListAdapter<CommentData.Data>()
    private lateinit var mData: ArticleContentData

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.articleContent.observe(this) {
            it?.apply {
                result(ArticleContentData(), error = { msg -> msg.toast() }, success = { data ->
                    mData = data
                    setContentShow()
                })
            }
        }
        mModel.value.commentList.observe(this) {
            it?.apply {
                result(CommentData(), error = { msg -> msg.toast() }, success = { data ->
                    mBinding.commentLayout.contentCommentTitle.text =
                        getString(R.string.string_title_comment, data.total)
                    if (data.isFirstPage) {
                        mCommentAdapter.putData(data.list)
                    } else {
                        mCommentAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.commentLayout.commentRefresh.setCanLoadMore(!data.isLastPage)
                })
            }
        }
        commonViewModel.value.focusResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    mBinding.commentBar.btnFocus.isSelected =
                        !mBinding.commentBar.btnFocus.isSelected
                }
            }
        }
        commonViewModel.value.commentPostResult.observe(this) {
            net.knowfx.yaodonghui.ext.dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg -> msg.toast() }, success = {
                    getString(R.string.string_comment_post_success).toast()
                    mBinding.commentBar.commentEdt.setText("")
                    mBinding.commentLayout.commentRefresh.getController()?.refresh()
                })
            }
        }
    }

    override fun getContentView(): View {
        mBinding = ActivityArticleConentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun setData(savedInstanceState: Bundle?) {
        mTargetId = bundle?.getInt("id", 0) ?: 0
        mFlag = bundle?.getString("flag") ?: ""
        mBinding.windowTitle.text = "${getTextFromModel(mFlag)}详情"
        mBinding.commentLayout.commentRv.layoutManager = LinearLayoutManager(this)
        mBinding.commentLayout.commentRv.addItemDecoration(CommonMarginDecoration(0, 0, 1, false))
        mBinding.commentLayout.commentRv.adapter = mCommentAdapter
        mBinding.commentLayout.commentRefresh.bindController(false) {
            requestComments(it)
        }
        requestContent()
        addListeners()
        mBinding.commentLayout.commentRefresh.getController()?.refresh()

    }

    private fun addListeners() {
        mBinding.commentBar.setListeners(
            onComment = { text ->//提交评论
                commonViewModel.value.postComment(id = mTargetId, model = mFlag, content = text)
            },
            seeAll = {//查看评论
                mBinding.scrollLayout.smoothScrollTo(0, mBinding.commentLayout.root.top)
            }, focus = {//关注/取消关注
                commonViewModel.value.focus(
                    id = mTargetId,
                    model = mFlag,
                    isFollow = !mData.follow
                )
            }, share = {//分享
                val data = ShareData(
                    title = mData.title,
                    content = if (mData.subTitle.isEmpty() || mData.subTitle == "null") getString(R.string.string_broker_share_content) else mData.subTitle,
                    url = hashMapOf<String, Any>(
                        Pair("id", mTargetId),
                        Pair("model", mFlag)
                    ).createShareUrl("ZX"),
                    thumbBitmap = getArticleShareBitmap(mFlag)
                )
                share(scroll = mBinding.scrollLayout, activity = this, data = data)
            })
        mBinding.btnBack.setOnclick {
            finish()
        }
        mBinding.dealer.root.setOnclick {
            jumpToTarget(
                this,
                BrokerContentActivity::class.java,
                hashMapOf(Pair("brokerId", mData.dealerId))
            )
        }
    }

    private fun requestContent() {
        //向服务器请求详情
        mModel.value.getArticleContent(mFlag, mTargetId)
    }

    private fun requestComments(page: Int) {
        //向服务器请求评论列表
        mModel.value.getCommentList(model = mFlag, id = mTargetId, page = page)
    }

    private fun setContentShow() {
        mBinding.commentBar.btnFocus.isSelected = mData.follow
        mBinding.articleTitleTv.text = mData.title
        mBinding.userThumbIv.intoCircle(mData.userhead)
        mBinding.userNameTv.text = mData.nickname
        mBinding.timeTv.text = mData.createTime.getCreateFormatTime()
        mBinding.contentTv.setHtml(
            mData.content,
            HtmlHttpImageGetter(mBinding.contentTv, null, true)
        )
        mBinding.contentTv.setOnClickATagListener { widget, spannedText, href ->
            jumpToTarget(
                this, WebActivity::class.java, hashMapOf(
                    Pair("url", href ?: ""),
                    Pair("title", spannedText.toString())
                )
            )
            true
        }
        if (mData.dealerId > 0) {
            mBinding.dealerLayout.visible()
            val params = mBinding.dealer.brokerIcon.layoutParams as ConstraintLayout.LayoutParams
            params.width = (params.height * 1.618).toInt()
            mBinding.dealer.brokerIcon.layoutParams = params
            val corner = resources.getDimensionPixelOffset(R.dimen.dp_5)
            mBinding.dealer.brokerIcon.intoCorners(
                url = mData.logofile,
                radius = corner.toFloat()
            )
            mBinding.dealer.brokerRankIcon.gone()
            mBinding.dealer.brokerWatch.gone()
            mBinding.dealer.labelText.text = mData.label
            mBinding.dealer.labelText.isSelected = mData.label != "监管中"
            mBinding.dealer.brokerName.text = mData.name
            mBinding.dealer.brokerMark.text = if (mData.labels.size > 0) {
                var marks = ""
                for (i in 0 until mData.labels.size) {
                    (i == 0) falseLet {
                        marks = marks.plus("|")
                    }
                    marks = marks.plus(mData.labels[i])
                }
                marks
            } else {
                "暂无"
            }
            mBinding.dealer.brokerScore.text = mData.grade
        } else {
            mBinding.dealerLayout.gone()
        }
    }

    override fun onDestroy() {
        mBinding.commentLayout.commentRefresh.unBindController()
        super.onDestroy()
    }
}