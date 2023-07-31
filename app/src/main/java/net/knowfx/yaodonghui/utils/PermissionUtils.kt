package net.knowfx.yaodonghui.utils

import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ui.views.PermissionDialog

/**
 * @ClassName: PermissionUtils
 * @Description: java类作用描述
 * @Author: Rain
 * @Version: 1.0
 */
object PermissionUtils {

    fun obtainPermission(
        activity: FragmentActivity,
        permissions: MutableList<String>,
        call: () -> Unit
    ) {
        PermissionX.init(activity)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList, _ ->
                val message = activity.getString(R.string.string_permission_agree)
                val dialog = PermissionDialog(activity, message, deniedList)
                scope.showRequestReasonDialog(dialog)
            }
            .onForwardToSettings { scope, deniedList ->
                val message = activity.getString(R.string.string_permission_setting)
                val dialog = PermissionDialog(activity, message, deniedList)
                scope.showForwardToSettingsDialog(dialog)
            }.request { allGranted, _, deniedList ->
                if (allGranted) {
                    call.invoke()
                } else {
                    "您拒绝了如下权限：$deniedList".toast()
                }
            }
    }
}