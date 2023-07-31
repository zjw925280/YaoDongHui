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
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityArticleSearchBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSearchHistoryBinding
import net.knowfx.yaodonghui.entities.ExploreListData
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
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.inputShowHide
import net.knowfx.yaodonghui.viewModels.ExploreViewModel

class ExploreSearchActivity : BaseActivity() {
    private val mBinding = lazy { ActivityArticleSearchBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[ExploreViewModel::class.java] }
    private var mKey = ""
    private val mAdapter = CommonListAdapter<ExploreListData.Data> { _, data, _ ->
        data as ExploreListData.Data
        //跳转到详情页面
        jumpToTarget(
            this, ExploreCommentContentActivity::class.java, hashMapOf(
                Pair("isSelf", false),
                Pair("id", data.id),
                Pair("layoutType", ExploreCommentContentActivity.LAYOUT_TYPE_EXPLORE),
                Pair("cover", data.logo)
            )
        )
    }

    private val mHistoryList = ArrayList<String>()
    private var historyKey = ""
    private var misHistoryActive = true

    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        mModel.value.exploreList.observe(this) {
            it?.apply {
                result(ExploreListData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(dataList = data.list)
                        mBinding.value.refresh.visible()
                        mBinding.value.historyLayout.gone()
                        if (data.list.isEmpty()) {
                            mBinding.value.layoutEmpty.root.visible()
                        } else {
                            mBinding.value.layoutEmpty.root.gone()
                        }
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
        historyKey = "explore_history_list"
        mBinding.value.apply {
            refresh.bindController(true) {
                mModel.value.searchExplore(mKey, it)
            }
            listRv.layoutManager = LinearLayoutManager(this@ExploreSearchActivity)
            listRv.adapter = mAdapter
        }
        val str = readCache(historyKey, "")
        if (str.isNotEmpty()) {
            mHistoryList.addAll(str.split(","))
        }

        mBinding.value.layoutEmpty.apply {
            noDataTv.text = getString(R.string.string_no_data_search)
            noDataIv.setImageLevel(1)
        }
        showHistory()
        addListeners()
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
                    mBinding.value.refresh.getController()?.refresh()
                }
                mBinding.value.layoutHistory.addView(binding.root)
            }
        }
    }

    private fun addListeners() {
        mBinding.value.searchEdt.setOnEditorActionListener { _, i, keyEvent ->
            val flag =
                i == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_SEARCH)
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
        mBinding.value.btnCancel.setOnclick {
            if (mKey.isEmpty()) {
                finish()
            } else {
                mKey = ""
                mBinding.value.searchEdt.setText("")
                mAdapter.putData(ArrayList())
                mBinding.value.refresh.gone()
                mBinding.value.layoutEmpty.root.gone()
                mBinding.value.historyLayout.visible()
            }
        }
        mBinding.value.btnDelete.setOnclick {
            mHistoryList.clear()
            delSingleCache(historyKey)
            showHistory()
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
            saveCache(historyKey, result)
            showHistory()
        }
    }


    private suspend fun releaseState() {
        kotlin.run {
            delay(200)
            misHistoryActive = true
        }
    }

    override fun onDestroy() {
        mBinding.value.refresh.unBindController()
        super.onDestroy()
    }

}