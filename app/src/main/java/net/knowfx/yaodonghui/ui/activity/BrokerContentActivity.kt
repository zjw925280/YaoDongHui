package net.knowfx.yaodonghui.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityBrokerContentBinding
import net.knowfx.yaodonghui.entities.BottomPopData
import net.knowfx.yaodonghui.entities.BrokerContentData
import net.knowfx.yaodonghui.entities.HelpData
import net.knowfx.yaodonghui.entities.IndexPagerData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.copy
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.registerLauncher
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.showDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.dialogs.DialogBottomList
import net.knowfx.yaodonghui.ui.dialogs.DialogHelpNotification
import net.knowfx.yaodonghui.ui.dialogs.DialogPlatformHelp
import net.knowfx.yaodonghui.ui.dialogs.DialogWebSet
import net.knowfx.yaodonghui.ui.views.AutoScrollRecyclerView
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.DealerViewModel


class BrokerContentActivity : BaseActivity() {
    private lateinit var mBinding: ActivityBrokerContentBinding
    private val mModel = lazy { ViewModelProvider(this)[DealerViewModel::class.java] }
    private var mBrokerId = 0
    private val mSuperviseAdapter =
        CommonListAdapter<BrokerContentData.SuperviseData> { _, data, _ ->
            data as BrokerContentData.SuperviseData
            //跳转到监管商详情
            val params = HashMap<String, Any>()
            params["id"] = data.id
            params["dealerId"] = mBrokerId
            jumpToTarget(this, SuperviseInfoActivity::class.java, params)
        }

    private val mExploreAdapter =
        CommonListAdapter<BrokerContentData.ExploreData> { _, data, _ ->
            data as BrokerContentData.ExploreData
            CoroutineScope(Dispatchers.Main).launch {
                //跳转到曝光详情
                jumpToTarget(
                    this@BrokerContentActivity,
                    ExploreCommentContentActivity::class.java,
                    hashMapOf(
                        Pair("id", data.id.toInt()),
                        Pair("isSelf", false),
                        Pair("layoutType", ExploreCommentContentActivity.LAYOUT_TYPE_EXPLORE),
                        Pair("cover", data.userhead)
                    )
                )
            }
        }

    private val mCommentAdapter =
        CommonListAdapter<BrokerContentData.CommentData> { _, data, _ ->
            data as BrokerContentData.CommentData
            CoroutineScope(Dispatchers.Main).launch {
                //跳转到交易商评论详情
                val params = HashMap<String, Any>()
                params["id"] = data.id.toInt()
                params["isSelf"] = false
                params["layoutType"] = ExploreCommentContentActivity.LAYOUT_TYPE_COMMENT
                params["cover"] = data.userhead
                jumpToTarget(
                    this@BrokerContentActivity,
                    ExploreCommentContentActivity::class.java,
                    params
                )
            }

        }

    private val mNewsAdapter =
        CommonListAdapter<IndexPagerData.ListData> { _, data, _ ->
            data as IndexPagerData.ListData
            //跳转到资讯详情
            jumpToTarget(
                this, ArticleContentActivity::class.java, hashMapOf(
                    Pair("id", data.id),
                    Pair("flag", data.model)
                )
            )
        }

    private lateinit var mData: BrokerContentData

    private lateinit var mHelpDialog: DialogPlatformHelp
    private lateinit var mHelpNotifyDialog: DialogHelpNotification
    private lateinit var mIdentityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        mIdentityLauncher = registerLauncher {
            it.data?.apply {
                if (getBooleanExtra("isSuccess", false)) {
                    jumpToTarget(
                        this@BrokerContentActivity,
                        LegalAidActivity::class.java,
                        hashMapOf(Pair("id", mBrokerId))
                    )
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(): View {
        mBinding = ActivityBrokerContentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun isUseFullScreenMode() = true

    override fun isUseBlackText() = false

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.dealerContent.observe(this) {
            it?.apply {
                result(BrokerContentData(), error = { msg -> msg.toast() }, success = { data ->
                    mData = data
                    setViews()
                })
            }
        }
        mModel.value.helpContent.observe(this) {
            it?.apply {
                result(HelpData(), error = { msg -> msg.toast() }, success = { data ->
                    mBinding.layoutHelp.helpCost.text =
                        getString(R.string.string_text_help_cost, (data.amount + data.unit))
                    mBinding.layoutHelp.superviseHelp.setOnclick {
                        this@BrokerContentActivity.checkIsLogin {
                            //我要援助
                            this@BrokerContentActivity::mHelpDialog.isInitialized.falseLet {
                                mHelpDialog = DialogPlatformHelp(data) {
                                    onHelpSubmit(data.describe3)
                                }
                            }
                            mHelpDialog.show(supportFragmentManager, "")
                        }
                    }
                })
            }
        }
        mModel.value.articleList.observe(this) {
            it?.apply {
                result(
                    IndexPagerData.PagerListData(),
                    error = { msg -> msg.toast() },
                    success = { data ->
                        if (data.isFirstPage) {
                            mNewsAdapter.putData(data.list)
                        } else {
                            mNewsAdapter.addDataListToEnd(data.list)
                        }
                    })
            }
        }

        commonViewModel.value.focusResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    mBinding.btnFocus.isSelected = !mData.follow
                    mData.follow = !mData.follow
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        mBrokerId = bundle?.getInt("brokerId") ?: 0
        mModel.value.getDealContent(id = mBrokerId)
        addListeners()
        super.setData(savedInstanceState)
    }

    private fun setViews() {
        mBinding.scoreTv.setText(mData.grade, 2)
        //设置资讯列表(普通)
        mBinding.newsRv.isNestedScrollingEnabled = false
        mBinding.newsRv.layoutManager = object : LinearLayoutManager(this@BrokerContentActivity) {
            override fun canScrollVertically() = false
        }
        mBinding.newsRv.adapter = mNewsAdapter
        mBinding.newsRefresh.bindController(false) {
            mModel.value.getArticleList(mBrokerId, it)
        }
        setLists(mData.isshow == 1, mData.iscomment == 1)
        val corner = resources.getDimensionPixelOffset(R.dimen.dp_5)
        val height = resources.getDimensionPixelOffset(R.dimen.dp_75)
        mBinding.copLogo.intoLogoOrCover(
            url = mData.logo,
            height = height,
            corner = corner
        )
        mBinding.copName.text = mData.fullName
        mBinding.countryIcon.intoCorners(
            url = mData.countrylogo,
            radius = resources.getDimensionPixelOffset(R.dimen.dp_2).toFloat()
        )
        var marks = ""
        (mData.lableNames.size > 0).trueLet {
            val sb = StringBuffer()
            mData.lableNames.forEach {
                if (it.isNullOrEmpty() || it == "null") return@forEach
                sb.append(it)
                sb.append("|")
            }
            sb.deleteCharAt(sb.lastIndexOf("|"))
            marks = sb.toString()
        }
        mBinding.copLabelText.text = marks.ifEmpty { "暂无" }
        mBinding.scoreTv.text = mData.grade.toString()
        mBinding.tradeEnv.text = mData.businessval
        mBinding.label.text = mData.appraisalval
        mBinding.webSet.text = mData.website
        showHideHelp(mData.ishelp == 1)
        mBinding.mailText.text = mData.email
        mBinding.phoneText.text = mData.phone
        mBinding.newsRefresh.getController()?.refresh()
        mBinding.banner.initBanner(mData.dealerScrollDTOS)
        mBinding.banner.addClickListener {
            if (it.seekId.isNotEmpty() && it.seekId != "null" && it.model.isNotEmpty() && it.model != "null") {
                jumpFromPush(it.model, it.seekId.toInt())
            }
        }
        mBinding.btnFocus.isSelected = mData.follow
        mBinding.copLabel.text = mData.label
        mBinding.copLabel.isSelected = mData.label != "监管中"
    }

    private fun showHideHelp(isShow: Boolean) {
        if (isShow) {
            mBinding.layoutHelp.root.visible()
            mBinding.layoutWarn.root.gone()
            mModel.value.getHelp(mBrokerId)

        } else {
            mBinding.layoutHelp.root.gone()
            mBinding.layoutWarn.root.visible()
            val totalWidth = resources.getDimensionPixelOffset(R.dimen.dp_85)
            val width = totalWidth * mData.riskGrade / 5
            val par = mBinding.layoutWarn.starProgress.layoutParams
            par.width = width
            mBinding.layoutWarn.starProgress.layoutParams = par
            mBinding.layoutWarn.btnWarn.setOnclick {
                //风险弹框
                val list = ArrayList<BottomPopData>()
                mData.riskDescription.forEach {
                    list.add(BottomPopData(R.mipmap.icon_warn, it))
                }
                DialogBottomList(bottomText = "关闭") {
                }.setDataList(list).show(this.supportFragmentManager, "")
            }
        }
    }

    /**设置界面上的监管信息列表，我要曝光列表，我要评论列表和资讯列表的数据显示*/
    private fun setLists(isExploreVis: Boolean = true, isCommentVis: Boolean = true) {
        //设置我要曝光列表(自动循环滚动)
        showHideExploreList(isExploreVis)
        //设置我要评论列表(自动循环滚动)
        showHideCommentList(isCommentVis)
        (this::mData.isInitialized).trueLet {
            //设置监管信息列表(全显示，不可滑动)
            mBinding.superviseRv.setHasFixedSize(true)
            mBinding.superviseRv.isNestedScrollingEnabled = false
            mBinding.superviseRv.layoutManager =
                object : LinearLayoutManager(this@BrokerContentActivity) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            mBinding.superviseRv.adapter = mSuperviseAdapter
            if (mData.regulatorsListDTOList.isNotEmpty()) {
                mSuperviseAdapter.putData(mData.regulatorsListDTOList)
                mBinding.layoutSupEmpty.gone()
                mBinding.superviseRv.visible()
            } else {
                mBinding.layoutSupEmpty.visible()
                mBinding.superviseRv.gone()
            }
        }
    }

    /**设置我要曝光列表相关布局是否显示*/
    private fun showHideExploreList(isShow: Boolean) {
        (isShow && mData.exposureInfoDTOList.isNotEmpty()).trueLet {
            mBinding.exploreBg.visible()
            mBinding.exploreText.visible()
            mBinding.exploreRv.visible()
            mBinding.exploreDiv.visible()
            mData.exposureInfoDTOList.isNotEmpty().trueLet {
                mBinding.exploreRv.isNestedScrollingEnabled = false
                mBinding.exploreRv.layoutManager = LinearLayoutManager(this@BrokerContentActivity)
                mBinding.exploreRv.adapter = mExploreAdapter
                mExploreAdapter.putData(mData.exposureInfoDTOList)
                mBinding.exploreRv.setSpeed(AutoScrollRecyclerView.ScrollSpeed.SPEED_SLOW)
                    .setOffsetCount(mData.exposureInfoDTOList.size / 3).startAutoScrolling()
            }
        }.elseLet {
            mBinding.exploreBg.gone()
            mBinding.exploreText.gone()
            mBinding.exploreRv.gone()
            mBinding.exploreDiv.gone()
        }
    }

    /**设置我要评论列表相关布局是否显示*/
    private fun showHideCommentList(isShow: Boolean) {
        (isShow && mData.reviewDTOList.isNotEmpty()).trueLet {
            mBinding.commentBg.visible()
            mBinding.commentText.visible()
            mBinding.commentRv.visible()
            mBinding.commentDiv.visible()
            mData.reviewDTOList.isNotEmpty().trueLet {
                mBinding.commentRv.isNestedScrollingEnabled = false
                mBinding.commentRv.layoutManager = LinearLayoutManager(this@BrokerContentActivity)
                mBinding.commentRv.adapter = mCommentAdapter
                mCommentAdapter.putData(mData.reviewDTOList)
                mBinding.commentRv.setSpeed(AutoScrollRecyclerView.ScrollSpeed.SPEED_SLOW)
                    .setOffsetCount(mData.reviewDTOList.size / 3).startAutoScrolling()
            }
        }.elseLet {
            mBinding.commentBg.gone()
            mBinding.commentText.gone()
            mBinding.commentRv.gone()
            mBinding.commentDiv.gone()
        }
    }

    private fun onHelpSubmit(content: ArrayList<String>) {
        this::mHelpNotifyDialog.isInitialized.falseLet {
            mHelpNotifyDialog = DialogHelpNotification(this@BrokerContentActivity, content) {
                onNotifyConfirmed()
            }
        }
        mHelpNotifyDialog.showDialog(this@BrokerContentActivity)
    }

    private fun onNotifyConfirmed() {
        mHelpDialog.dismiss()
        if (getUserData()?.iscert == 0) {
            "您还没有实名认证，请完善您的实名认证信息".toast()
            jumpToTargetForResult(this, AuthenticationActivity::class.java, mIdentityLauncher)
        } else {
            jumpToTarget(this, LegalAidActivity::class.java, hashMapOf(Pair("id", mBrokerId)))
        }
    }

    private fun addListeners() {
        setMultipleClick(
            mBinding.btnBack,
            mBinding.btnShare,
            mBinding.btnFocus,
            mBinding.webSetCopy,
            mBinding.exploreText,
            mBinding.commentText,
            mBinding.floatBtn,
            mBinding.mailText,
            mBinding.phoneText
        ) {
            when (it) {
                mBinding.btnBack -> {
                    //返回上个界面
                    this.finish()
                }

                mBinding.btnShare -> {
                    //分享
                    checkIsLogin {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                val bitmap = Glide.with(this@BrokerContentActivity).asBitmap()
                                    .load(mData.thumbLogo)
                                    .submit().get()
                                withContext(Dispatchers.Main) {
                                    val data = ShareData(
                                        title = getString(
                                            R.string.string_broker_share_title,
                                            mData.fullName
                                        ),
                                        content = getString(R.string.string_broker_share_content),
                                        url = hashMapOf<String, Any>(
                                            Pair("id", mBrokerId)
                                        ).createShareUrl("JYS"),
                                        thumbBitmap = bitmap
                                    )
                                    share(
                                        scroll = mBinding.scrollView,
                                        activity = this@BrokerContentActivity,
                                        data = data
                                    )
                                }
                            }
                        }
                    }
                }

                mBinding.btnFocus -> {
                    //关注
                    checkIsLogin {
                        commonViewModel.value.focus(model = "JYS", id = mBrokerId, !mData.follow)
                    }
                }

                mBinding.webSetCopy -> {
                    //复制网站
//                    DialogWebSet(mData.websites).show(supportFragmentManager, "")

                    Log.e("数据","数据="+mData.website.replace(",",""))
                    val url = mData.website.replace(",","") // Replace this with the URL you want to open
                   if(url.isNotEmpty()) {
                       val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                       startActivity(intent)
                   }else{
                       ToastUtils.showToast("链接是空的，请使用正确的链接")
                   }
//                    jumpToTarget(
//                        this, WebActivity::class.java, hashMapOf(
//                            Pair("url", mData.website.replace(",","")),
//                            Pair("title", mData.fullName)
//                        )
//                    )
                }

                mBinding.exploreText -> {
                    //跳转到我要曝光页面
                    checkIsLogin {
                        CommentExplorePostActivity.jumpToMe(
                            this,
                            layoutType = CommentExplorePostActivity.TYPE_EXPLORE,
                            brokerLogo = mData.logo,
                            brokerName = mData.fullName,
                            brokerId = mBrokerId
                        )
                    }
                }

                mBinding.commentText -> {
                    //跳转到我要评论页面
                    checkIsLogin {
                        CommentExplorePostActivity.jumpToMe(
                            this,
                            layoutType = CommentExplorePostActivity.TYPE_COMMENT,
                            brokerLogo = mData.logo,
                            brokerName = mData.fullName,
                            brokerId = mBrokerId
                        )
                    }
                }

                mBinding.floatBtn -> {
                    //返回首页
                    startActivity(Intent(this, MainActivity::class.java))
                }

                mBinding.mailText -> {
                    if (mBinding.mailText.text.toString().trim().isNotEmpty()) {
                        mBinding.mailText.copy()
                    }
                }//复制邮箱

                mBinding.phoneText -> {
                    if (mBinding.phoneText.text.toString().trim().isNotEmpty()) {
                        mBinding.phoneText.copy()
                    }
                }//复制手机号

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.newsRefresh.unBindController()
    }
}