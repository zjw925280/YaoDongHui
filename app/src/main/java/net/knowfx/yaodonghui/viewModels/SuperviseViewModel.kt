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

class SuperviseViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val dealerList = SingleSourceLiveData<Any>()
    val superviseContent = SingleSourceLiveData<Any>()
    val supProfile = SingleSourceLiveData<Any>()

    fun getSuperviseList(page: Int, size: Int) {
        dealerList.setSource(service.getSuperviseList(page, size))
    }

    fun getBrandList(page: Int, size: Int) {
        dealerList.setSource(service.getBrandList(page, size))
    }

    fun getBlockList(page: Int, size: Int) {
        dealerList.setSource(service.getBlockList(page, size))
    }

    fun getDealerList(page: Int, size: Int) {
        dealerList.setSource(service.getDealerList(page, size))
    }

    fun getSuperviseContent(id: Int, dealerId: Int) {
        superviseContent.setSource(service.getSuperviseContent(id, dealerId))
    }

    fun getSupProfile(id: Int) {
        supProfile.setSource(service.getSuperviseProfile(id))
    }

    fun getSupDealers(id: Int, page: Int) {
        dealerList.setSource(service.getSupDealer(id, page))
    }

    fun searchSupDealers(key: String, id: Int, page: Int) {
        dealerList.setSource(service.getSupDealer(key, id, page))
    }

    interface Service {
        @POST(APIs.URL_SUPERVISE_LIST)
        fun getSuperviseList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>

        @POST(APIs.URL_BRAND_LIST)
        fun getBrandList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>

        @POST(APIs.URL_BLOCK_LIST)
        fun getBlockList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>

        @POST(APIs.URL_DEALER_LIST)
        fun getDealerList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>

        @POST(APIs.URL_SUPERVISE_INFO)
        fun getSuperviseContent(
            @Query("id") id: Int,
            @Query("dealerId") dealerId: Int
        ): LiveData<Any>

        @POST(APIs.URL_SUPERVISE_PROFILE)
        fun getSuperviseProfile(
            @Query("id") id: Int
        ): LiveData<Any>

        @POST(APIs.URL_SUPERVISE_DEALER)
        fun getSupDealer(
            @Query("reguId") id: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_SUPERVISE_DEALER)
        fun getSupDealer(
            @Query("dealerName") key: String,
            @Query("reguId") id: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>


    }
}