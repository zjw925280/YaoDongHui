package net.knowfx.yaodonghui.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import net.knowfx.yaodonghui.utils.MyApplication


/**
 * @ClassName: NetworkUtil
 * @Description: 网络状态判断
 * @Author: Rain
 * @Version: 2.0
 */
object NetworkUtil {

    private var manager: ConnectivityManager? = null

    private fun initManager(): ConnectivityManager? {
        if (manager == null) {
            manager =
                (net.knowfx.yaodonghui.utils.MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)
        } else {
            manager as ConnectivityManager
        }
        return manager
    }

    /**
     * 判断是否有网络连接
     */
    fun isNetworkAvailable(): Boolean {
        var result = false
        val manager = initManager()
        manager?.run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true   //wifi
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true//数据
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true//以太网
                    else -> false
                }
            }
        }
        return result
    }

    /**
     * 判断是否是wifi下
     */
    fun isNetWifi(): Boolean {
        var result = false
        val manager = initManager()
        manager?.run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    else -> false
                }
            }
        }
        return result
    }

    /**
     * 判断是否是数据流量
     */
    fun isNetCellular(): Boolean {
        var result = false
        val manager = initManager()
        manager?.run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }

    /**
     * 判读是否是以太网
     */
    fun isNetEthernet(): Boolean {
        var result = false
        val manager = initManager()
        manager?.run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }

}