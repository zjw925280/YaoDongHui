package net.knowfx.yaodonghui.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * @ClassName: NetworkStateReceive
 * @Description: 网络状态判断
 * @Author: Rain
 * @Version: 1.0
 */
class NetworkStateReceive : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            if (!NetworkUtil.isNetworkAvailable()) {
                //收到没有网络时判断之前的值是不是有网络，如果有网络才提示通知 ，防止重复通知
                NetworkStateManager.instance.mNetworkStateCallback.value?.let {
                    if (it.isSuccess) {
                        NetworkStateManager.instance.mNetworkStateCallback.postValue(
                            NetState(
                                isSuccess = false
                            )
                        )
                    }
                }
            } else {
                //收到有网络时判断之前的值是不是没有网络，如果没有网络才提示通知 ，防止重复通知
                NetworkStateManager.instance.mNetworkStateCallback.value?.let {
                    if (!it.isSuccess) {
                        NetworkStateManager.instance.mNetworkStateCallback.postValue(
                            NetState(
                                isSuccess = true
                            )
                        )
                    }
                }
            }
        }
    }
}