package net.knowfx.yaodonghui.ui.fragment

import androidx.fragment.app.Fragment

abstract class BaseLoginFragment : Fragment() {
    abstract fun getParams(): HashMap<String, String>
}