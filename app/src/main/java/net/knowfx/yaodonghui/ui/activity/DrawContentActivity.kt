package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityDrawContentBinding
import net.knowfx.yaodonghui.databinding.LayoutDrawCircleContentBinding
import net.knowfx.yaodonghui.entities.CommentData
import net.knowfx.yaodonghui.entities.DrawContentData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getArticleShareBitmap
import net.knowfx.yaodonghui.ext.getByteArray
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setListeners
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.viewModels.ArticleViewModel
import kotlin.math.min


class DrawContentActivity : BaseActivity() {
    private var mIsActiveLoadMore = false
    private lateinit var mBinding: ActivityDrawContentBinding
    private val mViewModel = lazy { ViewModelProvider(this)[ArticleViewModel::class.java] }
    private val mCommentList = ArrayList<CommentData.Data>()
    private val mAdapter = CommonListAdapter<CommentData.Data>()
    private var mTargetId = 0
    private var mModel = ""
    private var mCover = ""
    private var mCommentCount = 0
    private lateinit var mData: DrawContentData
    override fun getContentView(): View {
        mBinding = ActivityDrawContentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mViewModel.value.articleContent.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                result(DrawContentData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    mData = data
                    setContent()
                })
            }
        }
        mViewModel.value.commentList.observe(this) {
            it?.apply {
                result(CommentData(), error = { msg -> msg.toast() }, success = { data ->
                    mCommentCount = data.total
                    mBinding.commentLayout.contentCommentTitle.text =
                        getString(R.string.string_title_comment, data.total)
                    mBinding.commentBar.commentNum.text = data.total.toString()
                    if (data.isFirstPage) {
                        mCommentList.clear()
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mCommentList.addAll(data.list)
                    resetRvHeight()
                    mBinding.commentLayout.commentRefresh.setEnableLoadMore(mIsActiveLoadMore && !data.isLastPage)
                })
            }
        }
        commonViewModel.value.commentPostResult.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg -> msg.toast() }, success = {
                    getString(R.string.string_comment_post_success).toast()
                    mBinding.commentBar.commentEdt.setText("")
                    mBinding.commentLayout.commentRefresh.getController()?.refresh()
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
    }


    override fun setData(savedInstanceState: Bundle?) {
        mTargetId = bundle?.getInt("id", 0) ?: 0
        mModel = bundle?.getString("code") ?: ""
        mCover = bundle?.getString("cover") ?: ""
        (mTargetId == 0 || mModel.isEmpty()).trueLet {
            "漫画内容不合法！".toast()
            finish()
        }
        mBinding.commentLayout.commentRv.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        requestData()
        mBinding.commentLayout.commentRv.setHasFixedSize(true)
        mBinding.commentLayout.commentRv.isVerticalScrollBarEnabled = false
        mBinding.commentLayout.commentRv.adapter = mAdapter
        mBinding.commentLayout.commentRefresh.apply {
            bindController(false) {
                requestComment(it)
            }
            getController()?.refresh()
        }
        setListeners()
        super.setData(savedInstanceState)
    }

    private fun setContent() {
        mBinding.drawTitleTv.text = mData.title
        mBinding.userThumbIv.intoCircle(mData.userhead)
        mBinding.userNameTv.text = mData.nickname
        mBinding.timeTv.text = mData.createTime
        val array = mData.content.split(",")
        createDrawContent(array)
        mBinding.commentBar.btnFocus.isSelected = mData.follow
    }

    private fun requestData() {
        showLoadingDialog()
        mViewModel.value.getArticleContent(id = mTargetId, model = mModel)
    }

    private fun requestComment(page: Int) {
        mViewModel.value.getCommentList(id = mTargetId, model = mModel, page = page)
    }

    private fun setListeners() {
        mBinding.btnBack.setOnclick { finish() }
        mBinding.commentBar.setListeners(onComment = { content ->
            //请求接口发送评论
            commonViewModel.value.postComment(id = mTargetId, model = mModel, content = content)
        },
            seeAll = {
                mBinding.layoutScroll.smoothScrollTo(0, mBinding.commentLayout.root.top)
            },
            focus = {
                commonViewModel.value.focus(
                    id = mTargetId,
                    model = mModel,
                    isFollow = !mBinding.commentBar.btnFocus.isSelected
                )
            }, share = {
                //分享
//                val
                val data = ShareData(
                    title = mData.title,
                    content = if (mData.subTitle.isEmpty() || mData.subTitle == "null") getString(R.string.string_broker_share_content) else mData.subTitle,
                    url = hashMapOf<String, Any>(
                        Pair("id", mTargetId),
                    ).createShareUrl("ZX"),
                    thumbBitmap = getArticleShareBitmap("JGXT")
                )
                share(
                    scroll = mBinding.layoutScroll,
                    activity = this,
                    data = data
                )
            })
    }

    private fun createDrawContent(array: List<String>) {
        array.forEach {
            it.isEmpty().trueLet { return@forEach }
            val binding = LayoutDrawCircleContentBinding.inflate(layoutInflater)
            binding.contentIv.into(url = it, start = {
                binding.contentLoading.visible()
            }, success = {
                binding.contentLoading.gone()
            })
            mBinding.drawContentLl.addView(binding.root)
        }
    }

    private fun resetRvHeight() {
        val count = min(mCommentList.size, MyApplication.COMMON_PAGE_SIZE)
        val itemHeight = resources.getDimension(R.dimen.dp_95)
        val param = mBinding.commentLayout.commentRv.layoutParams
        param.height = (itemHeight * count).toInt()
        mBinding.commentLayout.commentRv.layoutParams = param
    }

}