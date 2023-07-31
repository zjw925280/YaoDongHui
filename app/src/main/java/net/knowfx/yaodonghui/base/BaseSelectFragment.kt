package net.knowfx.yaodonghui.base

import androidx.fragment.app.Fragment

abstract class BaseSelectFragment : Fragment() {
    abstract fun setSelectMode(isSelect: Boolean)
    abstract fun selectAll(selected: Boolean)
    abstract fun getSelectedList(): ArrayList<Int>
    abstract fun finishUnFocused()
}