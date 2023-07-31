package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityComExpPostBinding
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.registerLauncher
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.imageSelector.SelectImageUtils
import net.knowfx.yaodonghui.viewModels.ExploreViewModel

class CommentExplorePostActivity : BaseActivity() {
    companion object {
        const val TYPE_COMMENT = 0
        const val TYPE_EXPLORE = 1
        fun jumpToMe(
            act: FragmentActivity,
            layoutType: Int = TYPE_COMMENT,
            brokerLogo: String = "",
            brokerName: String = "",
            brokerId: Int = 0,
            exploreType: Int = 1
        ) {
            val params = hashMapOf<String, Any>(
                Pair("layoutType", layoutType),
                Pair("logo", brokerLogo),
                Pair("name", brokerName),
                Pair("id", brokerId),
                Pair("exploreType", exploreType)
            )
            jumpToTarget(
                startAct = act, target = CommentExplorePostActivity::class.java, params = params
            )
        }
    }

    private lateinit var mBinding: ActivityComExpPostBinding
    private lateinit var mLauncher: ActivityResultLauncher<Intent>
    private val mModel = lazy { ViewModelProvider(this)[ExploreViewModel::class.java] }
    private var mLayoutType: Int = 0
    private lateinit var mBrokerLogo: String
    private lateinit var mBrokerName: String
    private var mBrokerId: Int = 0
    private var mExploreType = 0
    private var mDealerId = 0
    private val mImageList = ArrayList<PicData>()
    private val mImgAdapter = CommonListAdapter<PicData> { _, data, position ->
        data as PicData
        if (data.isEmpty()) {
            SelectImageUtils.dialogImage(
                this, true, 12 - (mImageList.size - 1), SelectImageUtils.MODE_MULTI
            ) {
                it?.apply {
                    if (isNotEmpty()) {
                        updatePic(this)
                    }
                }
            }
        }
    }

    private fun updatePic(list: ArrayList<PicData>) {
        mImgAdapter.delSingleData(mImgAdapter.getDataList<PicData>().size - 1)
        mImageList.addAll(list)
        mImgAdapter.addDataListToEnd(list)
        if (mImageList.size < 12) {
            mImgAdapter.addDataListToEnd(arrayListOf(PicData()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mLauncher = registerLauncher { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    mDealerId = getIntExtra("id", 0)
                    mBinding.brokerLogo.into(getStringExtra("logo") ?: "")
                    mBinding.brokerName.text = getStringExtra("name") ?: ""
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(): View {
        mBinding = ActivityComExpPostBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.postResult.observe(this) {
            dismissLoadingDialog()
            it?.result(String(), error = { msg -> msg.toast() }, success = {
                "曝光已经提交".toast()
                finish()
            }) ?: apply {
                "提交曝光失败，请稍后重试".toast()
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        mLayoutType = bundle?.getInt("layoutType", TYPE_COMMENT) ?: TYPE_COMMENT
        mBrokerLogo = bundle?.getString("logo") ?: ""
        mBrokerName = bundle?.getString("name") ?: ""
        mBrokerId = bundle?.getInt("id") ?: 0
        mExploreType = bundle?.getInt("exploreType", 0) ?: 0
        mBinding.brokerLogo.intoCorners(mBrokerLogo, resources.getDimension(R.dimen.dp_4))
        mBinding.brokerName.text = mBrokerName
        mBinding.photoRv.setHasFixedSize(true)
        mBinding.photoRv.isNestedScrollingEnabled = false
        mBinding.photoRv.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_6)
        mBinding.photoRv.addItemDecoration(CommonMarginDecoration(offset, offset, 3, false))
        mBinding.photoRv.adapter = mImgAdapter
        mImgAdapter.putData(arrayListOf(PicData()))
        switchLayouts()
        addListeners()
        super.setData(savedInstanceState)
    }

    private fun switchLayouts() {
        mBinding.windowTitle.text = when (mLayoutType) {
            TYPE_COMMENT -> {
                getString(R.string.string_text_i_comment)
            }

            else -> {
                getString(R.string.string_text_i_explore)
            }
        }
        mBinding.contentText.text = when (mLayoutType) {
            TYPE_COMMENT -> {
                getString(R.string.string_text_comment_content)
            }

            else -> {
                getString(R.string.string_text_explore_content)
            }
        }

        mBinding.brokerText.text = when (mLayoutType) {
            TYPE_COMMENT -> getString(R.string.string_text_broker_comment)
            else -> getString(R.string.string_text_broker_explore)
        }

        mBinding.titleEdt.hint =
            if (mLayoutType == TYPE_COMMENT)
                getString(R.string.string_hint_comment_title)
            else
                getString(R.string.string_hint_explore_title)

        mBinding.contentEdt.hint = when (mLayoutType) {
            TYPE_COMMENT -> {
                getString(R.string.string_hint_comment_content)
            }

            else -> {
                getString(R.string.string_hint_explore_content)
            }
        }

        (mLayoutType == TYPE_COMMENT).trueLet {//评论
            mBinding.typeSimple.gone()
            mBinding.typeText.gone()
            mBinding.typeSelector.gone()
        }.elseLet { //曝光
            mBinding.typeSimple.visible()
            mBinding.typeText.visible()
            mBinding.typeSelector.visible()
            mBinding.typeSelector.check(mBinding.typeSelector.children.elementAt(mExploreType).id)
        }
    }

    private fun addListeners() {
        mBinding.btnBack.setOnclick {
            finish()
        }
        mBinding.postBtn.setOnclick {
            val checkResult = if (mLayoutType == TYPE_COMMENT) {
                checkCommentParams()
            } else {
                checkExploreParams()
            }
            checkResult.isNotEmpty().trueLet {
                checkResult.toast()
            }.elseLet {
                if (mLayoutType == TYPE_COMMENT) doPostComment() else doPostExplore()
            }
        }
        mBinding.brokerMore.setOnclick {
            openDealerSelectActivity()
        }
    }

    private fun checkCommentParams(): String {
        return ""
    }

    private fun checkExploreParams(): String {
        val title = mBinding.titleEdt.text.toString().trim()
        val content = mBinding.contentEdt.toString().trim()
        if (title.isEmpty()) return "请填写曝光标题"
        if (content.isEmpty()) return "请填写曝光内容"
        if (mDealerId == 0) return "请选择曝光的企业"
        return ""
    }

    private fun doPostComment() {
        val title = mBinding.titleEdt.text.toString().trim()
        val content = mBinding.contentEdt.toString().trim()
        mModel.value.postComment(mDealerId, title, content, mImageList)
    }

    private fun doPostExplore() {
        showLoadingDialog()
        val title = mBinding.titleEdt.text.toString().trim()
        val content = mBinding.contentEdt.text.toString().trim()
        val exploreType =
            mBinding.typeSelector.indexOfChild(findViewById(mBinding.typeSelector.checkedRadioButtonId)) + 1
        mModel.value.postExplore(mDealerId, exploreType, title, content, mImageList)
    }

    private fun openDealerSelectActivity() {
        jumpToTargetForResult(
            this,
            BrokerSearchActivity::class.java,
            mLauncher,
            hashMapOf(Pair("isSelect", true))
        )
    }
}