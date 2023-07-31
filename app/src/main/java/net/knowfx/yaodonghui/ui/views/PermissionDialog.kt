package net.knowfx.yaodonghui.ui.views

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.permissionx.guolindev.dialog.RationaleDialog
import net.knowfx.yaodonghui.R

/**
 * @ClassName: PermissionDialog
 * @Description: 权限拒绝后的提示弹框
 * @Author: Rain
 * @Version: 1.0
 */
@TargetApi(30)
class PermissionDialog(
    context: Context,
    private val message: String,
    private val permissions: List<String>
) :
    RationaleDialog(context, R.style.CustomDialog) {

    private val permissionMap = mapOf(
        Manifest.permission.READ_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.WRITE_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.READ_CALL_LOG to Manifest.permission_group.CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG to Manifest.permission_group.CALL_LOG,
        Manifest.permission.PROCESS_OUTGOING_CALLS to Manifest.permission_group.CALL_LOG,
        Manifest.permission.CAMERA to Manifest.permission_group.CAMERA,
        Manifest.permission.READ_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.WRITE_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.GET_ACCOUNTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.RECORD_AUDIO to Manifest.permission_group.MICROPHONE,
        Manifest.permission.READ_PHONE_STATE to Manifest.permission_group.PHONE,
        Manifest.permission.READ_PHONE_NUMBERS to Manifest.permission_group.PHONE,
        Manifest.permission.CALL_PHONE to Manifest.permission_group.PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS to Manifest.permission_group.PHONE,
        Manifest.permission.ADD_VOICEMAIL to Manifest.permission_group.PHONE,
        Manifest.permission.USE_SIP to Manifest.permission_group.PHONE,
        Manifest.permission.ACCEPT_HANDOVER to Manifest.permission_group.PHONE,
        Manifest.permission.BODY_SENSORS to Manifest.permission_group.SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION to Manifest.permission_group.ACTIVITY_RECOGNITION,
        Manifest.permission.SEND_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_SMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_WAP_PUSH to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_MMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION to Manifest.permission_group.STORAGE
    )

    private val groupSet = HashSet<String>()
    private lateinit var messageText :TextView
    private lateinit var permissionsLayout : LinearLayout
    private lateinit var negativeBtn : Button
    private lateinit var positiveBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_dialog_layout)

        messageText = findViewById(R.id.messageText)
        permissionsLayout = findViewById(R.id.permissionsLayout)
        negativeBtn = findViewById(R.id.negativeBtn)
        positiveBtn = findViewById(R.id.positiveBtn)

        messageText.text = message
        buildPermissionsLayout()
        window?.let {
            val param = it.attributes
            val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            val height = param.height
            it.setLayout(width, height)
        }
    }

    private fun buildPermissionsLayout() {
        for (permission in permissions) {
            val permissionGroup = permissionMap[permission]
            if (permissionGroup != null && !groupSet.contains(permissionGroup)) {
                val textView = LayoutInflater.from(context)
                    .inflate(R.layout.permissions_item, permissionsLayout, false) as TextView
                textView.text = context.packageManager.getPermissionGroupInfo(permissionGroup, 0)
                    .loadLabel(context.packageManager)
                permissionsLayout.addView(textView)
                groupSet.add(permissionGroup)
            }
        }
    }

    override fun getNegativeButton(): View = negativeBtn

    override fun getPositiveButton(): View = positiveBtn

    override fun getPermissionsToRequest(): List<String> = permissions

}
