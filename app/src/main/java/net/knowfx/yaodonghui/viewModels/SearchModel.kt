package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.Single
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import retrofit2.http.POST
import retrofit2.http.Query

class SearchModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val dealerList = SingleSourceLiveData<Any>()
    val hotList = SingleSourceLiveData<Any>()

    fun searchDealer(param: String, pageNum: Int) {
        dealerList.setSource(service.searchDealer(param, pageNum))
    }

    fun getHotList(){
        hotList.setSource(service.getHotList())
    }

    interface Service {
        @POST(APIs.URL_DEALER_SEARCH)
        fun searchDealer(
            @Query("param") param: String,
            @Query("pageNum") pageNum: Int,
            @Query("pageSize") pageSize: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_DEALER_HOT)
        fun getHotList(): LiveData<Any>
    }
}