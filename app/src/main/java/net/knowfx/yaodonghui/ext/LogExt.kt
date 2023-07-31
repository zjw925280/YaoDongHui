package net.knowfx.yaodonghui.ext

import android.util.Log
import net.knowfx.yaodonghui.BuildConfig

fun String.logE(tag: String = "KNOWFX") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, this)
    }
}
fun String.logI(tag: String = "KNOWFX") {
    if (BuildConfig.DEBUG) {
        Log.i(tag, this)
    }
}
