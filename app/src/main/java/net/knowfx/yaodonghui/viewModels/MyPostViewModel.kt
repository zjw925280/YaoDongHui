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

class MyPostViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val dataList = SingleSourceLiveData<Any>()

    fun getExploreLit(status: Int = -1, page: Int) {
        dataList.setSource(
            if (status < 0) {
                service.getExploreList(page = page)
            } else {
                service.getExploreList(status = status, page = page)
            }
        )
    }

    fun getDealerCommentList(status: Int = -1, page: Int) {
        dataList.setSource(
            if (status < 0) {
                service.getDealerCommentList(page = page)
            } else {
                service.getDealerCommentList(status = status, page = page)
            }
        )
    }

    fun getOtherCommentList(status: Int = -1, page: Int) {
        dataList.setSource(
            if (status < 0) {
                service.getOtherCommentList(page = page)
            } else {
                service.getOtherCommentList(status = status, page = page)
            }
        )
    }

    interface Service {
        @POST(APIs.URL_MY_EXPLORE_POST)
        fun getExploreList(
            @Query("status") status: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_MY_EXPLORE_POST)
        fun getExploreList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>


        @POST(APIs.URL_MY_DEALER_COMMENT_POST)
        fun getDealerCommentList(
            @Query("status") status: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_MY_DEALER_COMMENT_POST)
        fun getDealerCommentList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>


        @POST(APIs.URL_MY_OTHER_COMMENT_POST)
        fun getOtherCommentList(
            @Query("status") status: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_MY_OTHER_COMMENT_POST)
        fun getOtherCommentList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

    }
}