package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query


class ExploreViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val exploreList = SingleSourceLiveData<Any>()
    val postResult = SingleSourceLiveData<Any>()

    fun getExploreList(dict: Int, page: Int, size: Int) {
        if (dict == 0) {
            exploreList.setSource(service.getExploreList(page, size))
        } else {
            exploreList.setSource(service.getExploreList(dict, page, size))
        }
    }

    fun searchExplore(key: String, page: Int) {
        exploreList.setSource(service.searchExplore(key, page))
    }

    fun postExplore(
        dealerId: Int,
        type: Int,
        title: String,
        content: String,
        pics: ArrayList<PicData> = ArrayList()
    ) {

        pics.isNotEmpty().trueLet {
            val multi = MultipartBody.Builder()
            pics.forEach {
                val file = it.getLocalFile()
                file?.apply {
                    val body = this.asRequestBody("multipart/form-data".toMediaType())
                    multi.addFormDataPart("exposurefile", this.name, body)
                }
            }
            multi.setType(MultipartBody.FORM)
            postResult.setSource(
                service.postExplore(
                    dealerId = dealerId,
                    type = type,
                    title = title,
                    content = content,
                    multiMap = multi.build().parts
                )
            )
        }.elseLet {
            postResult.setSource(
                service.postExplore(
                    dealerId = dealerId,
                    type = type,
                    title = title,
                    content = content,
                    multiMap = listOf(MultipartBody.Part.createFormData("exposurefile", ""))
                )
            )
        }
    }

    fun postComment(
        dealerId: Int,
        title: String,
        content: String,
        pics: ArrayList<PicData> = ArrayList()
    ) {

        pics.isNotEmpty().trueLet {
            val multi = MultipartBody.Builder()
            pics.forEach {
                val file = it.getLocalFile()
                file?.apply {
                    val body = this.asRequestBody("multipart/form-data".toMediaType())
                    multi.addFormDataPart("exposurefile", this.name, body)
                }
            }
            multi.setType(MultipartBody.FORM)
            postResult.setSource(
                service.postComment(
                    dealerId = dealerId,
                    title = title,
                    content = content,
                    multiMap = multi.build().parts
                )
            )
        }.elseLet {
            postResult.setSource(
                service.postComment(
                    dealerId = dealerId,
                    title = title,
                    content = content,
                    multiMap = listOf(MultipartBody.Part.createFormData("exposurefile", ""))
                )
            )
        }
    }

    interface Service {
        @POST(APIs.URL_EXPLORE_LIST)
        fun getExploreList(
            @Query("dictValue") dict: Int,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int,
        ): LiveData<Any>

        @POST(APIs.URL_EXPLORE_LIST)
        fun getExploreList(
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int,
        ): LiveData<Any>

        @POST(APIs.URL_GET_EXPLORE_LIST_SEARCH)
        fun searchExplore(
            @Query("param") param: String,
            @Query("pageNum") page: Int,
            @Query("pageSize") size: Int = MyApplication.COMMON_PAGE_SIZE
        ): LiveData<Any>

        @Multipart
        @POST(APIs.URL_EXPLORE_POST)
        fun postExplore(
            @Query("dealerId") dealerId: Int,
            @Query("TYPE") type: Int,
            @Query("title") title: String,
            @Query("content") content: String,
            @Part multiMap: List<MultipartBody.Part> = arrayListOf()
        ): LiveData<Any>

        @Multipart
        @POST(APIs.URL_COMMENT_POST)
        fun postComment(
            @Query("dealerId") dealerId: Int,
            @Query("title") title: String,
            @Query("content") content: String,
            @Part multiMap: List<MultipartBody.Part> = arrayListOf()
        ): LiveData<Any>
    }
}