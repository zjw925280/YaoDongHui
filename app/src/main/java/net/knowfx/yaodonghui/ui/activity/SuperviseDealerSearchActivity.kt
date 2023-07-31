package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityLayoutSearchBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSearchHistoryBinding
import net.knowfx.yaodonghui.entities.ItemData
import net.knowfx.yaodonghui.entities.SupBrokerData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.delSingleCache
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.readCache
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveCache
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.inputShowHide
import net.knowfx.yaodonghui.viewModels.SuperviseViewModel

class SuperviseDealerSearchActivity : BaseActivity() {
    private val mBinding = lazy { ActivityLayoutSearchBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[SuperviseViewModel::class.java] }
    private val mHistoryKey = "sup_dealer_history_list"
    private val mAdapter = CommonListAdapter<ItemData> { _, data, _ ->
        data as ItemData
        jumpToTarget(this, BrokerContentActivity::class.java, hashMapOf(Pair("brokerId", data.id)))
    }
    private val mHistoryList = ArrayList<String>()
    private var misHistoryActive = true
    private var mKey = ""
    private var mSupId = 0

    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        mModel.value.dealerList.observe(this) {
            it?.apply {
                result(SupBrokerData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(dataList = data.list)
                        mBinding.value.refresh.visible()
                        mBinding.value.historyLayout.gone()
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.value.refresh.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mSupId = bundle?.getInt("id") ?: 0
        val str = readCache(mHistoryKey, "")
        if (str.isNotEmpty()) {
            mHistoryList.addAll(str.split(","))
        }
        mBinding.value.apply {
            hotLayout.gone()
            listRv.layoutManager = LinearLayoutManager(this@SuperviseDealerSearchActivity)
            listRv.adapter = mAdapter
            refresh.bindController(true) {
                mModel.value.searchSupDealers(key = mKey, id = mSupId, page = it)
            }
        }
        showHistory()
        addListeners()
    }

    private fun addListeners() {
        mBinding.value.apply {
            searchEdt.setOnEditorActionListener { _, i, keyEvent ->
                val flag =
                    i == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
                flag.trueLet {
                    inputShowHide(mBinding.value.searchEdt, false)
                    val str = mBinding.value.searchEdt.text.toString().trim()
                    str.isNotEmpty().trueLet {
                        saveHistory(str)
                        mKey = str
                        mBinding.value.refresh.getController()?.refresh()
                    }.elseLet {
                        mAdapter.putData(ArrayList())
                        mBinding.value.refresh.gone()
                        mBinding.value.historyLayout.visible()
                    }
                }
                return@setOnEditorActionListener flag
            }

            btnCancel.setOnclick {
                if (mKey.isEmpty()) {
                    finish()
                } else {
                    mKey = ""
                    mBinding.value.searchEdt.setText("")
                    mAdapter.putData(ArrayList())
                    mBinding.value.refresh.gone()
                    mBinding.value.historyLayout.visible()
                }
            }
            mBinding.value.btnDelete.setOnclick {
                mHistoryList.clear()
                delSingleCache(mHistoryKey)
                showHistory()
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
            saveCache(mHistoryKey, result)
            showHistory()
        }
    }


    private suspend fun releaseState() {
        val str = readCache(mHistoryKey, "")
        if (str.isNotEmpty()) {
            mHistoryList.clear()
            mHistoryList.addAll(str.split(","))
        }
        showHistory()
        kotlin.run {
            delay(200)
            misHistoryActive = true
        }
    }

    private fun showHistory() {
        (mBinding.value.layoutHistory.childCount > 0).trueLet {
            mBinding.value.layoutHistory.removeAllViews()
        }
        mHistoryList.isEmpty().trueLet {
            mBinding.value.historyLayout.gone()
        }.elseLet {
            mBinding.value.historyLayout.visible()
            mHistoryList.forEach {
                val binding = LayoutItemSearchHistoryBinding.inflate(
                    layoutInflater, mBinding.value.layoutHistory, false
                )
                binding.root.text = it
                binding.root.setOnclick {
                    mBinding.value.searchEdt.setText(binding.root.text)
                    //触发搜索接口
                    mKey = binding.root.text.toString()
                    mBinding.value.refresh.autoRefresh()
                }
                mBinding.value.layoutHistory.addView(binding.root)
            }
        }
    }
}