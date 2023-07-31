package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityClassContentBinding
import net.knowfx.yaodonghui.entities.ClassContentData
import net.knowfx.yaodonghui.entities.CommentData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.getArticleShareBitmap
import net.knowfx.yaodonghui.ext.getByteArray
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getVideoUrl
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.viewModels.ClassViewModel


class ClassContentActivity : BaseActivity() {
    private lateinit var mBinding: ActivityClassContentBinding
    private lateinit var mOrientationUtil: OrientationUtils
    private val mModel = lazy { ViewModelProvider(this)[ClassViewModel::class.java] }
    private val mCommentAdapter = CommonListAdapter<CommentData.Data>()
    private var mTargetId = 0
    private var mPage = 1
    private var mEnableLoadMore = false
    private var mIsPlaying = false
    private var mIsPause = false
    private lateinit var mData: ClassContentData
    override fun getContentView(): View {
        mBinding = ActivityClassContentBinding.inflate(layoutInflater)
        initViews()
        return mBinding.root
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

    override fun isUseBlackText(): Boolean {
        return true
    }

    private fun initViews() {
        mBinding.commentRv.layoutManager = LinearLayoutManager(this)
        mBinding.commentRv.adapter = mCommentAdapter
    }

    override fun initViewModel() {
        mModel.value.classContent.observe(this) {
            it?.apply {
                result(ClassContentData(), error = { msg -> msg.toast() }, success = { data ->
                    mData = data
                    if (data.follow) {
                        mBinding.btnFocus.isSelected = true
                        mBinding.btnFocus.updateText("已关注")
                    } else {
                        mBinding.btnFocus.isSelected = false
                        mBinding.btnFocus.updateText("关注")
                    }
                    mBinding.timeTv.text = data.createTime.getCreateFormatTime()
                    mBinding.nameTv.text = data.nickname
                    mBinding.titleTv.text = data.title
                    mBinding.thumbIv.intoCircle(data.userhead)
                    mBinding.btnFocus.isSelected = data.follow
                    mBinding.commentTv.text =
                        getString(R.string.string_title_comment, data.totalComment)
                    initVideoPlayer(data.videoUrl, data.coverPicture, data.title)
                })
            }
        }
        mModel.value.classComment.observe(this) {
            it?.apply {
                result(CommentData(), error = { msg -> msg.toast() }, success = { data ->
                    data.isFirstPage.trueLet {
                        mCommentAdapter.putData(data.list)
                    }.elseLet {
                        mCommentAdapter.addDataListToEnd(data.list)
                    }
                    mEnableLoadMore = !data.isLastPage
                })
            }
        }
        mModel.value.commentPostResult.observe(this) {
            net.knowfx.yaodonghui.ext.dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg -> msg.toast() }, success = {
                    getString(R.string.string_comment_post_success).toast()
                    mBinding.commentEdt.setText("")
                    mPage = 1
                    requestCommentList()
                })
            }
        }
        commonViewModel.value.focusResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    mBinding.btnFocus.isSelected = !mBinding.btnFocus.isSelected
                }
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        mBinding.btnFocus.setIconAndText(R.drawable.selector_comment_focus, "关注")
        mBinding.btnFocus.isSelected = false
        mTargetId = bundle?.getInt("id", -1) ?: -1
        addListeners()
        mModel.value.getClassContent(mTargetId)
        requestCommentList()
    }

    private fun initVideoPlayer(url: String, cover: String, title: String) {
        mOrientationUtil = OrientationUtils(this, mBinding.videoPlayer)
        mOrientationUtil.isEnable = false
        val imageView = AppCompatImageView(this)
        val height = resources.getDimensionPixelOffset(R.dimen.dp_210)
        val params =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, height)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.into(cover)
        imageView.layoutParams = params
        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(false)
            .setUrl(url.getVideoUrl())
            .setCacheWithPlay(false)
            .setVideoTitle(title)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    mOrientationUtil.isEnable = true
                    mIsPlaying = true
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    if (this@ClassContentActivity::mOrientationUtil.isInitialized) {
                        mOrientationUtil.backToProtVideo()
                    }
                }
            }).build(mBinding.videoPlayer)
        mBinding.videoPlayer.backButton.gone()
        mBinding.videoPlayer.startPlayLogic()
    }

    private fun addListeners() {
        mBinding.btnBack.setOnclick {
            onBackPressed()
        }
        mBinding.videoPlayer.fullscreenButton.setOnclick {
            mOrientationUtil.resolveByClick()
            mBinding.videoPlayer.startWindowFullscreen(this, true, true)
        }
        mBinding.commentRv.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                (mEnableLoadMore && lastPosition == layoutManager.itemCount - 2).trueLet {
                    mPage += 1
                    requestCommentList()
                }
            }
        })
        mBinding.commentEdt.setOnEditorActionListener { _, actionId, _ ->
            checkIsLogin {
                (actionId == EditorInfo.IME_ACTION_SEND).trueLet {
                    val content = mBinding.commentEdt.text.toString().trim()
                    content.isEmpty().trueLet {
                        "请输入评论内容".toast()
                    }.elseLet {
                        showLoadingDialog()
                        //请求发送评论的接口
                        mModel.value.postComment(id = mTargetId, model = "JGXT", content = content)
                    }
                }
            }

            return@setOnEditorActionListener false
        }

        mBinding.btnFocus.setOnclick {
            commonViewModel.value.focus(
                model = "JGXT",
                id = mTargetId,
                !mBinding.btnFocus.isSelected
            )
        }

        mBinding.btnShare.setOnclick {
            //分享
            val data = ShareData(
                title = mData.title,
                content = if (mData.subTitle.isEmpty() || mData.subTitle == "null") getString(R.string.string_broker_share_content) else mData.subTitle,
                url = hashMapOf<String, Any>(
                    Pair("id", mTargetId),
                ).createShareUrl("JGXT"),
                thumbBitmap = getArticleShareBitmap("JGXT")
            )
            share(
                scroll = mBinding.scroll,
                activity = this,
                data = data
            )
        }
    }

    private fun requestCommentList() {
        mModel.value.getCommentList(id = mTargetId, model = "JGXT", page = mPage)
    }

    override fun onBackPressed() {
        this::mOrientationUtil.isInitialized.trueLet {
            mOrientationUtil.backToProtVideo()
        }
        GSYVideoManager.backFromWindowFull(this).trueLet {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        mBinding.videoPlayer.currentPlayer.onVideoPause()
        super.onPause()
        mIsPause = true
    }

    override fun onResume() {
        super.onResume()
        mIsPause.trueLet {
            return
        }
        mBinding.videoPlayer.currentPlayer.onVideoResume(false)
        mIsPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mIsPlaying) {
            mBinding.videoPlayer.currentPlayer.release()
        }
        this::mOrientationUtil.isInitialized.trueLet {
            mOrientationUtil.releaseListener()
        }
    }
}