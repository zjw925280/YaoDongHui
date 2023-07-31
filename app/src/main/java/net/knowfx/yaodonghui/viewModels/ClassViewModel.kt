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

class ClassViewModel(app: Application) : CommonViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val classList = SingleSourceLiveData<Any>()
    val classContent = SingleSourceLiveData<Any>()
    val classComment = SingleSourceLiveData<Any>()

    fun getClassList(page: Int, size: Int = MyApplication.COMMON_PAGE_SIZE, key: String = "") {
        if (key.isEmpty()) {
            classList.setSource(service.getClassList(page = page, size = size))
        } else {
            classList.setSource(service.getClassList(key = key, page = page, size = size))
        }
    }

    fun getClassContent(id: Int) {
        classContent.setSource(service.getClassContent(id))
    }

    fun getCommentList(id: Int, model: String, page: Int) {
        classComment.setSource(service.getCommentList(id, model, page))
    }

    interface Service {
        @POST(APIs.URL_CLASS_LIST)
        fun getClassList(
            @Query("searchParam") key: String,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>
        @POST(APIs.URL_CLASS_LIST)
        fun getClassList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int
        ): LiveData<Any>

        @POST(APIs.URL_CLASS_CONTENT)
        fun getClassContent(@Query("id") id: Int): LiveData<Any>

        @POST(APIs.URL_CLASS_COMMENT)
        fun getCommentList(
            @Query("id") id: Int,
            @Query("model") model: String,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>
    }
}