package net.knowfx.yaodonghui.entities

import kotlinx.parcelize.Parcelize
import net.knowfx.yaodonghui.base.BaseData
@Parcelize
class UserInfoData : BaseData() {
    /**用戶id*/
    var id = 0
    /**国家*/
    var country = ""
    /**头像地址*/
    var userhead = ""
    /**昵称*/
    var nickname = ""
    /**证件类型*/
    var typeId = ""
    /**手机号码*/
    var phone = ""
    /**性别*/
    var sex = 0
    /**是否实名认证（0：否；1：是）*/
    var iscert = 0
    /**证件号码*/
    var idcard = ""
    /**姓氏*/
    var surname = ""
    /**名字*/
    var name = ""
    /**手机号码*/
    var userId = ""

    fun isEmpty(): Boolean {
        return id == 0 && userhead.isEmpty() && nickname.isEmpty()
    }
}