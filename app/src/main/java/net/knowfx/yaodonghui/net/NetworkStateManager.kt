package net.knowfx.yaodonghui.net

import androidx.lifecycle.MutableLiveData


/**
 * @ClassName NetworkStateManager
 * @Author DYJ
 * @Date 2020/5/12 0:15
 * @Version 1.0
 * @Description  网络变化管理者
 */
class NetworkStateManager private constructor() {

    val mNetworkStateCallback = MutableLiveData<NetState>()

    init {
        //mNetworkStateCallback值为null时或者,不为空但是没有网络时才能初始化设值有网络
        if (mNetworkStateCallback.value == null || !mNetworkStateCallback.value!!.isSuccess) {
            mNetworkStateCallback.postValue(NetState(isSuccess = true))
        }
    }

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }
}