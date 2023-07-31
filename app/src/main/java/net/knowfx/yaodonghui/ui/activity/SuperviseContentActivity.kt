package net.knowfx.yaodonghui.ui.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.core.animation.addListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.ActivitySuperviseContentBinding
import net.knowfx.yaodonghui.entities.BrokerListData
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.BrokerData
import net.knowfx.yaodonghui.entities.ItemData
import net.knowfx.yaodonghui.entities.SupBrokerData
import net.knowfx.yaodonghui.entities.SuperviseProfileData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.formatTime
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.intoWithSize
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.viewHolders.SupBrokerHolder
import net.knowfx.yaodonghui.viewModels.SuperviseViewModel

class SuperviseContentActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySuperviseContentBinding
    private val mAdapter = CommonListAdapter<ItemData> { _, data, _ ->
        data as ItemData
        jumpToTarget(this, BrokerContentActivity::class.java, hashMapOf(Pair("brokerId", data.id)))
    }

    private val mModel = lazy { ViewModelProvider(this)[SuperviseViewModel::class.java] }

    private var mSuperviseId = 0

    override fun getContentView(): View {
        mBinding = ActivitySuperviseContentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun isUseBlackText() = false

    override fun isUseFullScreenMode() = true

    override fun getStatusBarColor(): Int {
        return R.color.color_6836CE
    }

    override fun initViewModel() {
        mModel.value.supProfile.observe(this) {
            it?.apply {
                result(SuperviseProfileData(), error = { msg -> msg.toast() }, success = { data ->
                    setViews(data)
                })
            }
        }
        mModel.value.dealerList.observe(this) {
            it?.apply {
                result(SupBrokerData(), error = { msg -> msg.toast() }, success = { data ->
                    val list = formatData(data.list)
                    if (data.isFirstPage) {
                        mAdapter.putData(dataList = list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = list)
                    }
                    mBinding.refreshLy.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    private fun formatData(list: ArrayList<ItemData>): ArrayList<ItemData> {
        val result = arrayListOf<ItemData>()
        for (i in 0 until list.size) {
            list[i].apply {
                if (fullName.isNotEmpty() && logofile.isNotEmpty()) {
                    result.add(this)
                }
            }
        }
        return result
    }

    private fun setViews(data: SuperviseProfileData) {
        mBinding.logoIv.intoLogoOrCover(
            data.filelogo,
            corner = resources.getDimensionPixelOffset(R.dimen.dp_5)
        )
        mBinding.titleTv.text = data.fullName
//        mBinding.yearTv.text = data.buildDate.formatTime("yyyy年")
        mBinding.typeTv.text = data.proper
        mBinding.infoTv.text = data.introduction
    }


    override fun setData(savedInstanceState: Bundle?) {
        mSuperviseId = bundle?.getInt("supId", 0) ?: 0
        (mSuperviseId == 0).trueLet {
            "数据错误，请稍后重试".toast()
            finish()
        }
        mBinding.listRv.layoutManager = LinearLayoutManager(this)
        mBinding.listRv.adapter = mAdapter
        mBinding.refreshLy.bindController(false) {
            mModel.value.getSupDealers(mSuperviseId, it)
        }
        addListeners()
        mModel.value.getSupProfile(mSuperviseId)
        mBinding.refreshLy.getController()?.refresh()
    }

    private fun addListeners() {
        setMultipleClick(mBinding.btnBack, mBinding.btnSearch, mBinding.moreBtn) {
            when (it) {
                mBinding.btnBack -> {
                    this.finish()
                }

                mBinding.btnSearch -> {
                    //跳转搜索界面
                    jumpToTarget(
                        this,
                        SuperviseDealerSearchActivity::class.java,
                        hashMapOf(Pair("id", mSuperviseId))
                    )
                }

                mBinding.moreBtn -> {
                    switchInfo()
                }

                else -> {}
            }
        }
    }


    private fun switchInfo() {
        val isSmall = mBinding.infoTv.maxLines == 2
        showArrowAnimation(isSmall)
    }

    private fun showArrowAnimation(isDown: Boolean) {
        val animator =
            ObjectAnimator.ofFloat(
                mBinding.moreBtn,
                "rotation",
                if (isDown) 180f else 0f, if (isDown) 0f else 180f
            )
        animator.duration = 100
        animator.start()
        animator.addListener(onEnd = {
            isDown.trueLet {
                mBinding.infoTv.maxLines = 100
            }.elseLet {
                mBinding.infoTv.maxLines = 2
            }
            mBinding.infoTv.invalidate()
            mBinding.infoCl.invalidate()
        })
    }
}