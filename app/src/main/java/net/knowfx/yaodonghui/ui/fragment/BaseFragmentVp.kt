package net.knowfx.yaodonghui.ui.fragment

import androidx.fragment.app.Fragment

open class BaseFragmentVp: Fragment() {
    open fun getViewPagerStatus(): Int{
        return 0
    }
}