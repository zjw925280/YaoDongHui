package net.knowfx.yaodonghui.base

import androidx.fragment.app.Fragment

abstract class BaseRefreshFragment: Fragment() {
    abstract fun refresh()
    abstract fun setCanScroll(canScroll: Boolean)
}