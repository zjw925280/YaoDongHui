package net.knowfx.yaodonghui.ui.dialogs

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.util.XPopupUtils
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.DialogPlatormHelpNotificationBinding
import net.knowfx.yaodonghui.databinding.LayoutItemHelpNotifyBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.viewHolders.BaseViewHolder

class DialogHelpNotification(
    context: Context,
    private val content: ArrayList<String>,
    private val onDismiss: (() -> Unit)?
) :
    CenterPopupView(context) {
    private val adapter = WarnAdapter()
    override fun getImplLayoutId() = R.layout.dialog_platorm_help_notification

    override fun initPopupContent() {
        val binding = DialogPlatormHelpNotificationBinding.inflate(LayoutInflater.from(context))
        val contentView = binding.root
        val params = if (contentView.layoutParams == null) {
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        } else {
            contentView.layoutParams as LayoutParams
        }
        params.gravity = Gravity.CENTER
        centerPopupContainer.addView(contentView, params)
        popupContentView.translationX = popupInfo.offsetX.toFloat()
        popupContentView.translationY = popupInfo.offsetY.toFloat()
        XPopupUtils.applyPopupSize(popupContentView as ViewGroup, maxWidth, maxHeight)
        binding.warnRv.isNestedScrollingEnabled = false
        binding.warnRv.layoutManager = object: LinearLayoutManager(context){
            override fun canScrollVertically() = false
        }
        binding.warnRv.adapter = adapter
        binding.btnKnow.setOnclick {
            onDismiss?.invoke()
            this.dismiss()
        }
    }

    inner class WarnAdapter : RecyclerView.Adapter<WarnHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WarnHolder(parent, R.layout.layout_item_help_notify)

        override fun getItemCount() = content.size

        override fun onBindViewHolder(holder: WarnHolder, position: Int) {
            holder.onBind(content[position])
        }

    }

    inner class WarnHolder(parent: ViewGroup, layoutId: Int) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    ) {
        fun onBind(
            str: String
        ) {
            val binding = LayoutItemHelpNotifyBinding.bind(itemView)
            binding.warnText.text = str
        }
    }
}