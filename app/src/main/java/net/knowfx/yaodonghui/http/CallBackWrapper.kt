package net.knowfx.yaodonghui.http

import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.utils.ErrorCode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallBackWrapper<R>(private val mCallBack: ResultCallback<R?>) : Callback<Result<R>?> {
    override fun onResponse(call: Call<Result<R>?>, response: Response<Result<R>?>) {
        val body = response.body()
        if (body != null) {
            val code = body.code
            if (code == 20000) {
                mCallBack.onSuccess(body.data)
            } else {
                val result = Result<R>()
                result.code = code
                result.msg = body.msg
                mCallBack.onFail(code, result.data)
            }
            if (BuildConfig.DEBUG) {
                ("url:" + call.request().url.toString()
                        + " ,code:" + body.code).logE()
            }
        } else {
            if (BuildConfig.DEBUG) {
                ("url:" + call.request().url.toString() + ", no response body").logE()
            }
            val result = Result<R>()
            result.code = ErrorCode.API_ERR_OTHER.code
            //            result.message = body.getMessage();
            mCallBack.onFail(ErrorCode.API_ERR_OTHER.code, result.data)
        }
    }

    override fun onFailure(call: Call<Result<R>?>, t: Throwable) {
        if (BuildConfig.DEBUG) {
            (call.request().url.toString() + " - " + t.message).logE()
        }
        val result = Result<R>()
        result.code = ErrorCode.API_ERR_OTHER.code
        result.msg = "请求失败"
        mCallBack.onFail(ErrorCode.API_ERR_OTHER.code, result.data)
    }
}