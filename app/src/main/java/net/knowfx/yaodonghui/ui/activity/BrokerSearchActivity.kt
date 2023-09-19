package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivityLayoutSearchBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSearchHistoryBinding
import net.knowfx.yaodonghui.entities.BrokerListData
import net.knowfx.yaodonghui.entities.SearchHotData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.inputShowHide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.BrokerData
import net.knowfx.yaodonghui.ext.KEY_BROKER_SEARCH_HISTORY
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.delSingleCache
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.readCache
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveCache
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.viewModels.SearchModel

class BrokerSearchActivity : BaseActivity() {
    private var mKey = ""
    private var mIsSelect = false
    private lateinit var mBinding: ActivityLayoutSearchBinding
    private val mModel = lazy { ViewModelProvider(this)[SearchModel::class.java] }
    private val mHistoryList = ArrayList<String>()
    private val mHotList = ArrayList<SearchHotData>()
    private val mHotAdapter =
        CommonListAdapter<SearchHotData> { _, data, _ ->
            data as SearchHotData
            //进入交易商详情
            jumpToTarget(
                this,
                BrokerContentActivity::class.java,
                hashMapOf(Pair("brokerId", data.dealerId))
            )
        }

    private val mSearchAdapter =
        CommonListAdapter<BrokerListData> { _, data, _ ->
            data as BrokerListData
            //item点击
            if (mIsSelect) {
                backForResult(data)
            } else {
                //进入交易商详情
                jumpToTarget(
                    this,
                    BrokerContentActivity::class.java,
                    hashMapOf(Pair("brokerId", data.id))
                )
            }
        }

    private var misHistoryActive = true
    override fun getContentView(): View {
        mBinding = ActivityLayoutSearchBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.dealerList.observe(this) {
            it?.result(BrokerData(), error = { msg -> msg.toast() }, success = { data ->
                data.isFirstPage.trueLet {
                    mSearchAdapter.putData(dataList = data.list)
                }.elseLet {
                    mSearchAdapter.addDataListToEnd(dataList = data.list)
                }
                mBinding.refresh.setCanLoadMore(!data.isLastPage)
            })

            (!mIsSelect).trueLet {
                if (mSearchAdapter.getDataList<BaseListData>().isEmpty()) {
                    mBinding.layoutEmpty.root.visible()
                } else {
                    mBinding.layoutEmpty.root.gone()
                }
                mBinding.refresh.visible()
                mBinding.historyLayout.gone()
                mBinding.hotLayout.gone()
            }.elseLet {
                mBinding.refresh.visible()
                mBinding.historyLayout.gone()
                mBinding.hotLayout.gone()
            }
        }
        mModel.value.hotList.observe(this) {
            it?.apply {
                result(
                    ArrayList<SearchHotData>(),
                    error = { msg -> msg.toast() },
                    success = { data ->
                        if (data.isNotEmpty()) {
                            mHotList.clear()
                            mHotList.addAll(data)
                            mBinding.hotLayout.visible()
                            mHotAdapter.putData(mHotList)
                        } else {
                            mBinding.hotLayout.gone()
                        }
                    })
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        initViews()
        setListeners()
        super.setData(savedInstanceState)
    }

    override fun onDestroy() {
        mBinding.refresh.unBindController()
        super.onDestroy()
    }

    private fun initViews() {
        mIsSelect = bundle?.getBoolean("isSelect", false) ?: false
        //设置热门搜索模块
        mBinding.hotRv.setHasFixedSize(true)
        mBinding.hotRv.isNestedScrollingEnabled = false
        val offsetV = resources.getDimensionPixelOffset(R.dimen.dp_20)
        mBinding.hotRv.addItemDecoration(CommonMarginDecoration(offsetV, 0, 4, false))
        mBinding.hotRv.layoutManager = object : GridLayoutManager(this, 4) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mBinding.hotRv.adapter = mHotAdapter
        mHotList.isEmpty().trueLet {
            mBinding.hotLayout.gone()
        }.elseLet {
            mBinding.hotLayout.visible()
        }
        mBinding.refresh.bindController(true) {
            mModel.value.searchDealer(mKey, it)
        }
        mBinding.listRv.layoutManager = LinearLayoutManager(this)
        mBinding.listRv.addItemDecoration(
            CommonMarginDecoration(
                resources.getDimensionPixelOffset(R.dimen.dp_20),
                0,
                1,
                true
            )
        )
        mBinding.listRv.adapter = mSearchAdapter
        mIsSelect.trueLet {
            mBinding.refresh.visible()
            mBinding.refresh.getController()?.refresh()
            mBinding.historyLayout.gone()
            mBinding.hotLayout.gone()
        }.elseLet {
            mBinding.refresh.gone()
            //设置搜索历史模块
            val str = readCache(KEY_BROKER_SEARCH_HISTORY, "")
            if (str.isNotEmpty()) {
                mHistoryList.addAll(str.split(","))
            }
            mModel.value.getHotList()
            showHistory()
            checkIsShowHot()
        }

        mBinding.layoutEmpty.apply {
            noDataTv.text = getString(R.string.string_no_data_search)
            noDataIv.setImageLevel(1)
        }

    }

    private fun checkIsShowHot() {
        mHotList.isEmpty().trueLet {
            mBinding.hotLayout.gone()
        }.elseLet {
            mBinding.hotLayout.visible()
        }
    }


    private fun setListeners() {
        mBinding.searchEdt.setOnEditorActionListener { _, i, keyEvent ->
            val flag =
                i == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
            flag.trueLet {
                inputShowHide(mBinding.searchEdt, false)
                val str = mBinding.searchEdt.text.toString().trim()
                str.isNotEmpty().trueLet {
                    saveHistory(str)
                }
                mKey = str
                mBinding.refresh.autoRefresh()
            }
            return@setOnEditorActionListener flag
        }
        setMultipleClick(mBinding.btnCancel, mBinding.btnDelete) {
            when (it) {
                mBinding.btnCancel -> {
                    val str = mBinding.searchEdt.text.toString().trim()
                    if (str.isEmpty() || mIsSelect) {
                        finish()
                    } else {
                        mBinding.searchEdt.setText("")
                        mSearchAdapter.putData(arrayListOf())
                        mBinding.refresh.gone()
                        mBinding.layoutEmpty.root.gone()
                        showHistory()
                        checkIsShowHot()
                    }
                }

                else -> {
                    mHistoryList.clear()
                    showHistory()
                    delSingleCache(KEY_BROKER_SEARCH_HISTORY)
                }
            }
        }
    }

    private fun saveHistory(text: String) {
        misHistoryActive.trueLet {
            misHistoryActive = false
            CoroutineScope(Dispatchers.Main).launch {
                releaseState()
            }
            (mHistoryList.size == 10).trueLet {
                mHistoryList.removeLast()
            }
            when (val index = mHistoryList.indexOf(text)) {
                -1 -> {
                    mHistoryList.add(0, text)
                }

                0 -> {

                }

                else -> {
                    mHistoryList.removeAt(index)
                    mHistoryList.add(0, text)
                }
            }
            var result = ""
            mHistoryList.forEach {
                result = result.plus("$it,")
            }
            result = result.substring(0, result.lastIndexOf(","))
            saveCache(KEY_BROKER_SEARCH_HISTORY, result)
            showHistory()
        }
    }

    private suspend fun releaseState() {
        kotlin.run {
            delay(200)
            misHistoryActive = true
        }
    }

    private fun showHistory() {
        (mBinding.layoutHistory.childCount > 0).trueLet {
            mBinding.layoutHistory.removeAllViews()
        }
        mHistoryList.isEmpty().trueLet {
            mBinding.historyLayout.gone()
        }.elseLet {
            mBinding.historyLayout.visible()
            mHistoryList.forEach {
                val binding = LayoutItemSearchHistoryBinding.inflate(
                    layoutInflater, mBinding.layoutHistory, false
                )
                binding.root.text = it
                binding.root.setOnclick {
                    mBinding.searchEdt.setText(binding.root.text)
                    //触发搜索接口
                    mKey = binding.root.text.toString()
                    mBinding.refresh.autoRefresh()
                }
                mBinding.layoutHistory.addView(binding.root)
            }
        }
    }

    private fun backForResult(data: BrokerListData) {
        val intent = Intent()
        intent.putExtra("id", data.id)
        intent.putExtra("logo", data.logo.ifEmpty { data.logofile })
        intent.putExtra("name", data.name.ifEmpty { data.fullName })
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}