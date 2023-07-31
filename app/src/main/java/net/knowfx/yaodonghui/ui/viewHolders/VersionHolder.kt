package net.knowfx.yaodonghui.ui.viewHolders

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemVersionBinding
import net.knowfx.yaodonghui.entities.VersionData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible

class VersionHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as VersionData
        val binding = LayoutItemVersionBinding.bind(itemView)
        binding.textContent.text =
            HtmlCompat.fromHtml(data.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.textTitle.text = data.version
        binding.textTitle.setOnclick {
            showArrowAnimation(binding, !binding.textContent.isVisible)
        }
    }


    private fun showArrowAnimation(binding: LayoutItemVersionBinding, isDown: Boolean) {
        val animator =
            ObjectAnimator.ofFloat(
                binding.ivArrow,
                "rotation",
                if (isDown) 180f else 0f, if (isDown) 0f else 180f
            )
        animator.duration = 100
        animator.start()
        animator.addListener(onEnd = {
            isDown.trueLet {
                binding.textContent.visible()
            }.elseLet {
                binding.textContent.gone()
            }
            itemView.invalidate()
            itemView.invalidate()
        })
    }
}