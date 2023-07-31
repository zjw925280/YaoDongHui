package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.base.BaseResponse
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ext.getFileStr
import net.knowfx.yaodonghui.ext.requestBody
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query


class SettingViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)

    val userInfo = SingleSourceLiveData<Any>()
    val eventList = SingleSourceLiveData<Any>()
    val versionList = SingleSourceLiveData<Any>()
    val profile = SingleSourceLiveData<Any>()

    fun changeAvatar(data: PicData) {
        val file = data.getLocalFile()
        file?.apply {
            val fileBody = file.asRequestBody("application/file".toMediaType())
            val part = MultipartBody.Part.createFormData("headfile", name, fileBody)
            userInfo.setSource(service.changeAvatar(part))
        }
    }

    fun changeNickname(name: String) {
        userInfo.setSource(service.changeNickname(name))
    }

    fun changeSex(sex: Int) {
        userInfo.setSource(service.changeSex(sex))
    }

    fun getBrandEvent() {
        eventList.setSource(service.getBrandEvent())
    }

    fun getVersions() {
        versionList.setSource(service.getVersions())
    }

    fun getProfile() {
        profile.setSource(service.getProfile())
    }

    interface Service {
        @Multipart
        @POST(APIs.URL_CHANGE_AVATAR)
        fun changeAvatar(@Part body: MultipartBody.Part): LiveData<Any>

        @POST(APIs.URL_CHANGE_NICKNAME)
        fun changeNickname(@Query("nickname") name: String): LiveData<Any>

        @POST(APIs.URL_CHANGE_SEX)
        fun changeSex(@Query("sexual") sex: Int): LiveData<Any>

        @POST(APIs.URL_GET_BRAND_EVENT)
        fun getBrandEvent(): LiveData<Any>

        @POST(APIs.URL_GET_VERSIONS)
        fun getVersions(): LiveData<Any>

        @POST(APIs.URL_GET_PROFILE)
        fun getProfile(): LiveData<Any>

    }
}