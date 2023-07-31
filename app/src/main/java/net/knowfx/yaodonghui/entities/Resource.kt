package net.knowfx.yaodonghui.entities

import android.text.TextUtils
import net.knowfx.yaodonghui.utils.ErrorCode

/**
 * 资源结果包装类，此类反应资源获取的状态和结果
 *
 * @param <T>
</T> */
class Resource<T> {
    val status: Status
    val message: String?
    val code: Int
    val data: T?

    constructor(status: Status, data: T?, code: Int) {
        this.status = status
        this.data = data
        this.code = code
        message = ErrorCode.fromCode(code).errorMessage
    }

    constructor(status: Status, data: T?, code: Int, msg: String?) {
        this.status = status
        this.data = data
        this.code = code
        message = if (TextUtils.isEmpty(msg)) ErrorCode.fromCode(code).errorMessage else msg
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val resource = o as Resource<*>
        if (status != resource.status) {
            return false
        }
        if (if (message != null) message != resource.message else resource.message != null) {
            return false
        }
        return if (data != null) data == resource.data else resource.data == null
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

    companion object {
        fun <T> success(data: T?): Resource<T?> {
            return Resource(Status.SUCCESS, data, ErrorCode.NONE_ERROR.code)
        }

        fun <T> success(data: T?, code: Int, message: String?): Resource<T?> {
            return Resource(Status.SUCCESS, data, code, message)
        }

        fun <T> error(code: Int, data: T?): Resource<T?> {
            return Resource(Status.ERROR, data, code)
        }

        fun <T> error(code: Int, data: T?, msg: String?): Resource<T?> {
            return Resource(Status.ERROR, data, code, msg)
        }

        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data, ErrorCode.NONE_ERROR.code)
        }
    }
}