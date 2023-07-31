package net.knowfx.yaodonghui.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.ActivityFunctionAllBinding
import net.knowfx.yaodonghui.entities.AllFunctionList
import net.knowfx.yaodonghui.entities.CommonTitleData
import net.knowfx.yaodonghui.entities.IndexFunctionListData
import net.knowfx.yaodonghui.ext.indexFunctionJump
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveData
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.LayoutTypes
import net.knowfx.yaodonghui.viewModels.IndexViewModel

class FunctionAllActivity : BaseActivity() {
    companion object {
        const val KEY_LIST = "list_function_all"
    }

    private lateinit var mBinding: ActivityFunctionAllBinding
    private val mModel = lazy { ViewModelProvider(this)[IndexViewModel::class.java] }
    override fun getContentView(): View {
        mBinding = ActivityFunctionAllBinding.inflate(layoutInflater)
        mBinding.btnBack.setOnclick {
            finish()
        }
        return mBinding.root
    }

    private val mAdapterFun =
        CommonListAdapter<IndexFunctionListData.IndexFunctionData> { view, data, position ->
            //点击直通车中的功能item
            data as IndexFunctionListData.IndexFunctionData
            indexFunctionJump(data)
        }

    private val mAdapterSuper =
        CommonListAdapter<AllFunctionList.SuperviseFunctionData> { view, data, position ->
            //点击监管机构的item
            data as AllFunctionList.SuperviseFunctionData
            jumpToTarget(
                this,
                SuperviseContentActivity::class.java,
                hashMapOf(Pair("supId", data.id))
            )
        }

    override fun initViewModel() {
        mModel.value.functionAllList.observe(this) {
            it?.apply {
                result(AllFunctionList(), error = { msg -> msg.toast() }, success = { data ->
                    //保存数据
                    saveData(KEY_LIST, Gson().toJson(data))
                    //如果未设置列表，则设置数据，如果设置过数据，则忽略代码块中的内容
                    mAdapterFun.getDataList<BaseListData>().isEmpty().trueLet {
                        mAdapterFun.putData(data.bannerClassDTOList)
                        mAdapterSuper.putData(data.regulatorlogoDTOList)
                    }
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        //设置列表的属性和数据适配器
        mBinding.funRv.layoutManager = GridLayoutManager(this, 5)
        mBinding.superRv.layoutManager = GridLayoutManager(this, 5)
        val offsetV = resources.getDimensionPixelOffset(R.dimen.dp_18)
        mBinding.funRv.addItemDecoration(CommonMarginDecoration(offsetV, 0, 5, false))
        mBinding.superRv.addItemDecoration(CommonMarginDecoration(offsetV, 0, 5, false))
        mBinding.funRv.adapter = mAdapterFun
        mBinding.superRv.adapter = mAdapterSuper
        requestData()
    }

    /**从服务器/本地获取列表数据*/
    private fun requestData() {
        mModel.value.getAllFunctions()
    }
}