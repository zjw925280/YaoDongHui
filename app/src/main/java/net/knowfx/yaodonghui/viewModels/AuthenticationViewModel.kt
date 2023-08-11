package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import retrofit2.http.POST
import retrofit2.http.Query

class AuthenticationViewModel(app: Application) : CommonViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)

//    val phoneCodeResult = SingleSourceLiveData<Any>()

    val submitResult = SingleSourceLiveData<Result<Boolean>>()

    fun submitAuthentication(surname: String, name: String, code: String, idcard: String) {
        submitResult.setSource(service.submitAuthentication(surname, name, idcard, code))
    }

//    /**
//     * 请求手机验证码
//     */
//    fun requestPhoneCode(phone: String) {
//        phoneCodeResult.setSource(service.requestPhoneCode(phone))
//    }

    interface Service {
//        @POST(APIs.URL_GET_PHONE_CODE)
//        fun requestPhoneCode(@Query("phone") phone: String): LiveData<Any>

        @POST(APIs.URL_SUBMIT)
        fun submitAuthentication(
            @Query("surname") surname: String,
            @Query("name") name: String,
            @Query("idcard") idcard: String,
            @Query("code") code: String,
            @Query("country") country: String = "CN",
            @Query("typeId") typeId: String = "身份证"
        ): LiveData<Result<Boolean>>
    }
}