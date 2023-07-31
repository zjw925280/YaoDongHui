package net.knowfx.yaodonghui.http

/**
 * 执行 Task 的回调
 * @param <Result> 请求成功时的结果类
</Result> */
interface ResultCallback<Result> {
    fun onSuccess(result: Result)
    fun onFail(errorCode: Int, result: Result)
}