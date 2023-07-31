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

class ArticleViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)

    val articleContent = SingleSourceLiveData<Any>()

    val articleList = SingleSourceLiveData<Any>()

    val commentList = SingleSourceLiveData<Any>()

    fun getArticleContent(model: String, id: Int) {
        articleContent.setSource(service.getArticleContent(model, id))
    }

    fun getTruthList(page: Int) {
        articleList.setSource(service.getTruthList(page))
    }

    fun searchTruthList(page: Int, key: String) {
        articleList.setSource(service.getTruthList(page = page, key = key))
    }

    fun getSherlockList(page: Int) {
        articleList.setSource(service.getSherlockList(page))
    }

    fun searchSherlockList(page: Int, key: String) {
        articleList.setSource(service.getSherlockList(page = page, key = key))
    }

    fun getSnakeList(page: Int) {
        articleList.setSource(service.getSnakeList(page))
    }

    fun searchSnakeList(page: Int, key: String) {
        articleList.setSource(service.getSnakeList(page = page, key = key))
    }

    fun getDrawList(page: Int) {
        articleList.setSource(service.getDrawList(page))
    }

    fun searchDrawList(key: String = "", page: Int) {
        articleList.setSource(service.getDrawList(key, page))
    }

    fun getCommentList(model: String, id: Int, page: Int) {
        commentList.setSource(service.getArticleCommentList(model, id, page))
    }


    interface Service {
        @POST(APIs.URL_ARTICLE_CONTENT)
        fun getArticleContent(@Query("model") model: String, @Query("id") id: Int): LiveData<Any>

        @POST(APIs.URL_GET_TRUTH_LIST)
        fun getTruthList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_GET_TRUTH_LIST)
        fun getTruthList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE,
            @Query("searchParam") key: String
        ): LiveData<Any>

        @POST(APIs.URL_GET_SHERLOCK_LIST)
        fun getSherlockList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_GET_SHERLOCK_LIST)
        fun getSherlockList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE,
            @Query("searchParam") key: String
        ): LiveData<Any>

        @POST(APIs.URL_GET_SNAKE_LIST)
        fun getSnakeList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_GET_SNAKE_LIST)
        fun getSnakeList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE,
            @Query("searchParam") key: String
        ): LiveData<Any>
        @POST(APIs.URL_DRAW_CIRCLE_LIST)
        fun getDrawList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>
        @POST(APIs.URL_DRAW_CIRCLE_LIST)
        fun getDrawList(
            @Query("searchParam") key: String,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @POST(APIs.URL_GET_COMMENT)
        fun getArticleCommentList(
            @Query("model") model: String,
            @Query("id") id: Int,
            @Query("pageNum") pageNum: Int
        ): LiveData<Any>
    }
}