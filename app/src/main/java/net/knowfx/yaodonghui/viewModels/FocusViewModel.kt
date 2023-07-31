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

class FocusViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val articleList = SingleSourceLiveData<Any>()
    val dealerList = SingleSourceLiveData<Any>()

    fun getFocusDealer(page: Int){
        dealerList.setSource(service.getFocusDealer(page))
    }

    fun getFocusArticle(page: Int){
        articleList.setSource(service.getFocusArticle(page))
    }

    interface Service {
        @POST(APIs.URL_MY_FOCUS_DEALER)
        fun getFocusDealer(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_MY_FOCUS_ARTICLE)
        fun getFocusArticle(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>
    }
}