package net.knowfx.yaodonghui.base

import net.knowfx.yaodonghui.utils.ErrorCode

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述： 基本返回数据类型
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
open class BaseResponse : BaseData() {
    var code = 0
    var msg: String = "解析异常"
    var captchaEnabled=false
    var uuid: String = ""
    var img: String = ""

    fun isSuccess(): Boolean{
        return ErrorCode.fromCode(code) == ErrorCode.SUCCESS
    }
}