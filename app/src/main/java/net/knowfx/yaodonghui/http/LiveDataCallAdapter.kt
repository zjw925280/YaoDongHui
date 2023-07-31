package net.knowfx.yaodonghui.http

import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.utils.ErrorCode
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.net.ConnectException
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<R>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<R> {
        return object : LiveData<R>() {
            var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            var body = response.body()
                            // 当没有信息体时通过 http code 判断业务错误
                            if (body == null && !response.isSuccessful) {
                                val result: Result<*> = Result<Any?>()
                                try {
                                    body = result as R
                                } catch (e: Exception) {
                                    // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                }
                            }
                            postValue(body)
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            if (BuildConfig.DEBUG) {
                                ("onFailure:" + call.request().url.toString() + ", error:" + throwable.message).logE()
                            }
                            if (throwable is ConnectException) {
                                var body: R? = null
                                val result: Result<*> = Result<Any?>()
                                result.code = ErrorCode.NETWORK_ERROR.code
                                try {
                                    body = result as R
                                } catch (e: Exception) {
                                    // 可能部分接口并不是由 result 包裹，此时无法获取错误码
                                }
                                postValue(body)
                            } else {
                                postValue(null)
                            }
                        }
                    })
                }
            }
        }
    }
}