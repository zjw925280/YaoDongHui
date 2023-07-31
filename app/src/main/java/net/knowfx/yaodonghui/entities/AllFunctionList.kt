package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class AllFunctionList {
    val bannerClassDTOList = ArrayList<IndexFunctionListData.IndexFunctionData>()
    val regulatorlogoDTOList = ArrayList<SuperviseFunctionData>()

    class SuperviseFunctionData: BaseListData(LayoutTypes.TYPE_FUNCTION_SUPERVISE_ITEM){
        val id = 0
        val logofile = ""
        val country = ""
        val name = ""
    }
}