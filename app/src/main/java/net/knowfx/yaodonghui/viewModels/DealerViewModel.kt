package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import retrofit2.http.POST
import retrofit2.http.Query

class DealerViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val dealerContent = SingleSourceLiveData<Any>()
    val helpContent = SingleSourceLiveData<Any>()
    val articleList = SingleSourceLiveData<Any>()
    val postResult = SingleSourceLiveData<Result<Int>>()

    fun getDealContent(id: Int) {
        dealerContent.setSource(service.getDealerContent(id))
    }

    fun getHelp(id: Int) {
        helpContent.setSource(service.getHelp(id))
    }

    fun getArticleList(id: Int, pageNum: Int) {
        articleList.setSource(service.getArticle(id, pageNum))
    }

    fun postHelp(dealerId: Int, account: String, email: String){
        postResult.setSource(service.postHelp(dealerId, account, email))
    }

    interface Service {
        @POST(APIs.URL_DEALER_CONTENT)
        fun getDealerContent(@Query("id") id: Int): LiveData<Any>

        @POST(APIs.URL_GET_HELP)
        fun getHelp(@Query("id") id: Int): LiveData<Any>

        @POST(APIs.URL_GET_DEALER_ARTICLE)
        fun getArticle(
            @Query("id") id: Int,
            @Query("pageNum") pageNum: Int,
            @Query("pageSize") pageSize: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_POST_HELP)
        fun postHelp(
            @Query("dealerId") id: Int,
            @Query("account") account: String,
            @Query("email") email: String
        ): LiveData<Result<Int>>
    }
}