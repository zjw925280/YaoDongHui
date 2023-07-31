package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import retrofit2.http.POST
import retrofit2.http.Query

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)

    val historyList = SingleSourceLiveData<Any>()

    val delResult = SingleSourceLiveData<Result<*>>()

    fun getTopHistory(page: Int) {
        historyList.setSource(service.getTopHistory(page))
    }

    fun getSeeHistory(page: Int) {
        historyList.setSource(service.getSeeHistory(page))
    }

    fun getMyHistory(page: Int) {
        historyList.setSource(service.getMyHistory(page))
    }

    fun delMyHistory() {
        delResult.setSource(service.delMyHistory())
    }

    interface Service {
        @POST(APIs.URL_HISTORY_TOP)
        fun getTopHistory(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_HISTORY_SEE)
        fun getSeeHistory(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>


        @POST(APIs.URL_HISTORY_MY)
        fun getMyHistory(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_HISTORY_MY_DEL)
        fun delMyHistory(): LiveData<Result<*>>

    }
}