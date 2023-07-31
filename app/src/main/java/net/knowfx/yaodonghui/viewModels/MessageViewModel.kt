package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import retrofit2.http.POST
import retrofit2.http.Query

class MessageViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)

    val pushList = SingleSourceLiveData<Any>()
    val msgList = SingleSourceLiveData<Any>()
    val newestMsg = SingleSourceLiveData<Any>()

    fun getPushList(page: Int) {
        pushList.setSource(service.getPushList(page))
    }

    fun getMessageList(page: Int) {
        msgList.setSource(service.getMessageList(page))
    }

    fun getNewestMsg(){
        newestMsg.setSource(service.getNewestMsg())
    }

    interface Service {
        @POST(APIs.URL_PUSH_HISTORY)
        fun getPushList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_MSG_HISTORY)
        fun getMessageList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_NEW_MESSAGE)
        fun getNewestMsg(): LiveData<Any>
    }
}