package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.ImageAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityExploreCommentBinding
import net.knowfx.yaodonghui.entities.BottomPopData
import net.knowfx.yaodonghui.entities.ExploreCommentContentData
import net.knowfx.yaodonghui.entities.ImageData
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getExploreCommentBitmap
import net.knowfx.yaodonghui.ext.getLabelStr
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.dialogs.DialogBottomList
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.getExploreTitleWithValue
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class ExploreCommentContentActivity : BaseActivity() {
    companion object {
        const val LAYOUT_TYPE_EXPLORE = 1
        const val LAYOUT_TYPE_COMMENT = 2
    }

    private lateinit var mBinding: ActivityExploreCommentBinding
    private val mPicAdapter = ImageAdapter<ImageData>(this)


    private var mTargetId = 0
    private var mIsSelf = false
    private var mLayoutType = LAYOUT_TYPE_EXPLORE
    private lateinit var mDialog: DialogBottomList
    private lateinit var mShareData: ShareData
    private var mCover = ""
    override fun getContentView(): View {
        mBinding = ActivityExploreCommentBinding.inflate(layoutInflater)
        initObserver()
        return mBinding.root
    }


    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mIsSelf = bundle?.getBoolean("isSelf", false) ?: false
        mTargetId = bundle?.getInt("id", 0) ?: 0
        mLayoutType = bundle?.getInt("layoutType") ?: LAYOUT_TYPE_EXPLORE
        mCover = bundle?.getString("cover") ?: ""

        mBinding.windowTitle.text = when (mLayoutType) {
            LAYOUT_TYPE_EXPLORE -> {
                getString(R.string.string_text_explore_title)
            }

            else -> {
                mBinding.labelTv.text = getString(R.string.string_text_comment)
                getString(R.string.string_text_comment_title)
            }
        }
        switchIfSelf(mIsSelf)

        mBinding.picRv.isNestedScrollingEnabled = false
        mBinding.picRv.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollHorizontally() = false
            override fun canScrollVertically() = false
        }
        val gap = resources.getDimensionPixelOffset(R.dimen.dp_10)
        mBinding.picRv.addItemDecoration(CommonMarginDecoration(gap, gap, 3, false))
        mBinding.picRv.adapter = mPicAdapter
        if (mLayoutType == LAYOUT_TYPE_EXPLORE) {
            commonViewModel.value.getExploreContent(mTargetId)
        } else {
            commonViewModel.value.getCommentContent(mTargetId)
        }
        val array = arrayListOf(
            "内容不实",
            "辱骂攻击",
            "内容无意义",
            "诱导诈骗",
            "地域攻击",
            "泄露隐私",
            "内容抄袭",
            "内容抄袭",
            "种族歧视",
            "其他"
        )
        mDialog = DialogBottomList(bottomText = "取消") {
            showLoadingDialog()
            mBinding.root.postDelayed({
                dismissLoadingDialog()
                "举报已提交".toast()
            }, 1000)
        }
        val list = ArrayList<BottomPopData>()
        array.forEach {
            val data = BottomPopData(0, it)
            list.add(data)
        }
        mDialog.setDataList(list)
        mBinding.btnReport.setOnclick {
            checkIsLogin {
                mDialog.show(supportFragmentManager, "")
            }
        }
        mBinding.btnBack.setOnclick { finish() }
    }

    private fun initObserver() {
        commonViewModel.value.exploreCommentContent.observe(this) {
            it?.apply {
                result(ExploreCommentContentData(),
                    error = { msg -> msg.toast() },
                    success = { data ->
                        setViews(data)
                    })
            }
        }
    }

    private fun setViews(data: ExploreCommentContentData) {
        mShareData = ShareData(
            title = data.title,
            content = data.content,
            url = hashMapOf<String, Any>(
                Pair("id", mTargetId),
                Pair("flag", if (mLayoutType == LAYOUT_TYPE_EXPLORE) "BG" else "JYS")
            ).createShareUrl("BG"),
            thumbBitmap = getExploreCommentBitmap(mLayoutType, data.type)
        )
        mBinding.titleTv.text = data.title
        mBinding.avatarIv.intoCircle(data.userhead)
        mBinding.nameTv.text = data.nickname
        mBinding.contentTv.setHtml(
            data.content,
            HtmlHttpImageGetter(mBinding.contentTv, null, true)
        )
        mBinding.timeTv.text = data.createTime.getCreateFormatTime()
        mBinding.layoutDealer.labelText.text = data.label
        mBinding.layoutDealer.labelText.isSelected = data.label != "监管中"
        val corner = resources.getDimensionPixelOffset(R.dimen.dp_5)
        mBinding.layoutDealer.brokerIcon.intoLogoOrCover(data.logofile, corner = corner)
        mBinding.layoutDealer.brokerName.text = data.name
        mBinding.layoutDealer.brokerMark.text = data.labels.getLabelStr()
        mBinding.layoutDealer.brokerScore.text = data.grade
        mBinding.layoutDealer.brokerRankIcon.gone()
        mBinding.layoutDealer.brokerRankText.gone()
        mBinding.layoutDealer.brokerWatch.gone()
        val picList = ArrayList<ImageData>()
        data.files.forEach {
            val bean = ImageData()
            bean.picServicePath = it
            picList.add(bean)
        }
        mPicAdapter.putData(picList)
        if (mLayoutType == LAYOUT_TYPE_EXPLORE) {
            mBinding.labelTv.text = getExploreTitleWithValue(data.type)
        } else {
            mBinding.labelTv.gone()
        }
        mBinding.btnBack.setOnclick {
            finish()
        }
        mBinding.layoutDealer.root.setOnclick {
            jumpToTarget(
                this,
                BrokerContentActivity::class.java,
                hashMapOf(Pair("brokerId", data.dealerId))
            )
        }
        mBinding.btnShare.setOnclick {
            checkIsLogin {
                if (this::mShareData.isInitialized) {
                    CoroutineScope(Dispatchers.Main).launch {
                        share(
                            scroll = mBinding.scroll,
                            activity = this@ExploreCommentContentActivity,
                            data = mShareData
                        )
                    }
                }
            }
        }
        mBinding.btnBack.setOnclick { finish() }
    }

    private fun switchIfSelf(isSelf: Boolean) {
        isSelf.trueLet {
            mBinding.btnDelete.visible()
            mBinding.btnReport.gone()
        }.elseLet {
            mBinding.btnDelete.gone()
            mBinding.btnReport.visible()
        }
    }
}