package net.knowfx.yaodonghui.utils

import android.os.Build

/**
 * Author  : lht
 * Date    : 2019/8/27
 */
object SystemUtil {
    /**
     * 整个产品的名称
     *
     * @return 整个产品的名称
     */
    val product: String
        get() = Build.PRODUCT

    /**
     * 设备标识
     *
     * @return string
     */
    val systemSign: String
        get() = Build.FINGERPRINT
}