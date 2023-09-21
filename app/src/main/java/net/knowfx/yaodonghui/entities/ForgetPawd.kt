package net.knowfx.yaodonghui.entities

data class ForgetPawd(
    val phone: String,
    val code: String,
    val newPassword: String
)
