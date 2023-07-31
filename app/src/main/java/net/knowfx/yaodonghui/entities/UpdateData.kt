package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseData

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：更新bean
 * 作者：
 * 创建时间：2018/8/28
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
class UpdateData : BaseData() {
    var version: String? = null
    var downloadUrl: String? = null
    //描述
    var preview: String? = null
}