package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityMessageBinding
import net.knowfx.yaodonghui.entities.MessageData
import net.knowfx.yaodonghui.entities.PushData
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.viewModels.MessageViewModel

class MessageActivity : BaseActivity() {
    private val mBinding = lazy { ActivityMessageBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[MessageViewModel::class.java] }
    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        mModel.value.newestMsg.observe(this) {
            it?.apply {
                result(NewestMessage(), error = { msg -> msg.toast() }, success = { data ->
                    mBinding.value.apply {
                        contentMessage.text = data.message?.title ?: "暂无"
                        contentMessage.text = data.prope?.content?.ifEmpty { "暂无" } ?: "暂无"
                    }
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        mBinding.value.btnBack.setOnclick { finish() }
        mBinding.value.layoutMessage.setOnclick {
            jumpToTarget(this, MessageHistoryActivity::class.java)
        }
        mBinding.value.pushLayout.setOnclick {
            jumpToTarget(
                this,
                PushHistoryActivity::class.java,
                hashMapOf(Pair("title", "推送消息"))
            )
        }
        mModel.value.getNewestMsg()
    }

    data class NewestMessage(
        val message: MessageData? = null,
        val prope: PushData? = null
    )
}