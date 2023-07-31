package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.utils.NetConfig

/**
 * 网络请求结果基础类
 *
 * @param <T> 请求结果的实体类
</T> */
class Result<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null

    constructor() {}

    override fun toString(): String {
        return "Result{" +
                "code=" + code +
                ", message='" + msg + '\'' +
                ", data=" + data +
                '}'
    }

    val isOk: Boolean
        get() = code == 200

    constructor(code: Int) {
        this.code = code
    }

    val isSuccess: Boolean
        get() = code == NetConfig.REQUEST_SUCCESS_CODE
}