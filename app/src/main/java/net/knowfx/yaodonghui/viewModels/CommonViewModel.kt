package net.knowfx.yaodonghui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.entities.AdData
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Query

open class CommonViewModel(app: Application) : AndroidViewModel(app) {

    private val commonService1 = HttpClientManager.getInstance(app,"https://www.knowfx.net").client1.createService( Service::class.java)
    private val commonService = HttpClientManager.getInstance(app).client.createService(Service::class.java)
    val userInfoResult = SingleSourceLiveData<Any>()
    val logoutResult = SingleSourceLiveData<Any>()
    val delAccountResult = SingleSourceLiveData<Any>()
    val commentPostResult = SingleSourceLiveData<Any>()
    val commentDelResult = SingleSourceLiveData<Result<Int>>()
    val focusResult = SingleSourceLiveData<Result<Boolean>>()
    val unFocusResult = SingleSourceLiveData<Result<Int>>()
    val token = SingleSourceLiveData<Any>()
    val exploreCommentContent = SingleSourceLiveData<Any>()
    val feedbackResult = SingleSourceLiveData<Result<Boolean>>()
    val contract = SingleSourceLiveData<Any>()
    val adResult = SingleSourceLiveData<Result<AdData>>()
    val commentContent = SingleSourceLiveData<Any>()
    val graphicCodeResult = SingleSourceLiveData<Any>()
    val phoneUuidCodeResult = SingleSourceLiveData<Any>()


    fun getMyCommentContent(id: Int){
        commentContent.setSource(commonService.getMyCommentContent(id))
    }
    fun requestUserInfo() {
        userInfoResult.setSource(commonService.requestUserInfo())
    }

    fun logout() {
        logoutResult.setSource(commonService.logout())
    }

    fun delAccount() {
        delAccountResult.setSource(commonService.delAccount())
    }

    fun postComment(id: Int, model: String, content: String) {
        commentPostResult.setSource(commonService.postComment(model, id, content))
    }

    fun delComment(ids: String, model: String) {
        commentDelResult.setSource(commonService.delComment(ids, model))
    }

    fun focus(model: String, id: Int, isFollow: Boolean) {
        focusResult.setSource(commonService.focus(model, id, isFollow))
    }

    fun unFocus(ids: String) {
        unFocusResult.setSource(commonService.unFocus(ids))
    }

    fun refreshToken() {
        token.setSource(commonService.refreshToken())
    }

    fun getExploreContent(id: Int) {
        exploreCommentContent.setSource(commonService.getExploreContent(id))
    }

    fun getCommentContent(id: Int) {
        exploreCommentContent.setSource(commonService.getCommentContent(id))
    }
    /**
     * 获取图形验证码
     */
    fun getGraphicCode() {
        graphicCodeResult.setSource(commonService1.getGraphicCode())
    }
    fun feedback(theme: String, content: String, phone: String, pics: ArrayList<PicData>) {
        val parts = HashMap<String, List<MultipartBody.Part>>()
        pics.isNotEmpty().trueLet {
            val multi = MultipartBody.Builder()
            pics.forEach {
                val file = it.getLocalFile()
                file?.apply {
                    val body = this.asRequestBody("multipart/form-data".toMediaType())
                    multi.addFormDataPart("file", this.name, body)
                }
            }
            multi.setType(MultipartBody.FORM)
            parts["feedbackfile"] = multi.build().parts
        }.elseLet {
            parts["feedbackfile"] = listOf()
        }
        feedbackResult.setSource(
            commonService.feedback(
                theme = theme,
                content = content,
                phone = phone,
                feedbackfile = parts
            )
        )
    }

    fun getPrivacy(){
        contract.setSource(commonService.getContract("YSXY"))
    }

    fun getUserContract(){
        contract.setSource(commonService.getContract("YHXY"))
    }

    fun getAd(){
        adResult.setSource(commonService.getAd())
    }

    /**
     * 请求手机验证码
     */
    fun requestUuidPhoneCode(phone: String,code: String,uuid: String,type: String) {
        Log.e("主要是这个uuid","主要是这个uuid=="+uuid)
        phoneUuidCodeResult.setSource(commonService1.requestUuidPhoneCode(phone,code,uuid,type))
    }

    interface Service {

        @POST(APIs.URL_GET_PHONE_UUID_CODE)
        fun requestUuidPhoneCode(@Query("phone") phone: String,@Query("code") code: String,@Query("uuid") uuid: String,@Query("type") type: String): LiveData<Any>


        @GET(APIs.URL_GETCODE)
        fun getGraphicCode(): LiveData<Any>

        @POST(APIs.URL_GET_USER_INFO)
        fun requestUserInfo(): LiveData<Any>

        @POST(APIs.URL_LOGOUT)
        fun logout(): LiveData<Any>

        @POST(APIs.URL_DELETE_ACCOUNT)
        fun delAccount(): LiveData<Any>

        @POST(APIs.URL_COMMENT_POST_COMMON)
        fun postComment(
            @Query("model") model: String,
            @Query("dealerId") id: Int,
            @Query("content") content: String,
        ): LiveData<Any>

        @POST(APIs.URL_COMMENT_DEL_COMMON)
        fun delComment(@Query("ids") ids: String, @Query("model") model: String): LiveData<Result<Int>>

        @POST(APIs.URL_FOLLOW_COMMON)
        fun focus(
            @Query("model") model: String,
            @Query("modelId") id: Int,
            @Query("follow") isFollow: Boolean
        ): LiveData<Result<Boolean>>

        @POST(APIs.URL_UN_FOLLOW_COMMON)
        fun unFocus(
            @Query("ids") id: String,
        ): LiveData<Result<Int>>

        @POST(APIs.URL_REFRESH_TOKEN)
        fun refreshToken(): LiveData<Any>

        @POST(APIs.URL_EXPLORE_CONTENT)
        fun getExploreContent(@Query("id") id: Int): LiveData<Any>

        @POST(APIs.URL_COMMENT_CONTENT)
        fun getCommentContent(@Query("id") id: Int): LiveData<Any>

        @Multipart
        @POST(APIs.URL_FEEDBACK)
        fun feedback(
            @Query("title") theme: String,
            @Query("content") content: String,
            @Query("phone") phone: String,
            @PartMap feedbackfile: HashMap<String, List<MultipartBody.Part>>
        ): LiveData<Result<Boolean>>

        @POST(APIs.URL_FEEDBACK)
        fun feedback(
            @Query("title") theme: String,
            @Query("content") content: String,
            @Query("phone") phone: String
        ): LiveData<Result<Boolean>>

        @POST(APIs.URL_GET_CONTRACT)
        fun getContract(
            @Query("type") type: String,
        ): LiveData<Any>

        @POST(APIs.URL_GET_COMMENT_CONTENT)
        fun getMyCommentContent(
            @Query("id") id: Int,
        ): LiveData<Any>

        @GET(APIs.URL_SPLASH_AD)
        fun getAd(): LiveData<Result<AdData>>
    }
}