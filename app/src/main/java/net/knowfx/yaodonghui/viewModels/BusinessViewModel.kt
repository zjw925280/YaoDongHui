package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

class BusinessViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val result = SingleSourceLiveData<Result<Boolean>>()

    fun submit(
        name: String,
        email: String,
        purpose: String,
        pics: ArrayList<PicData> = ArrayList()
    ) {
        pics.isNotEmpty().trueLet {
            val part = MultipartBody.Builder()
            pics.forEach {
                val file = it.getLocalFile()
                file?.apply {
                    val body = this.asRequestBody("multipart/form-data".toMediaType())
                    part.addFormDataPart("cooperatefile", this.name, body)
                }
            }
            part.setType(MultipartBody.FORM)
        }.elseLet {
            result.setSource(
                service.submit(
                    name,
                    email,
                    purpose,
                    listOf(MultipartBody.Part.createFormData("cooperatefile", ""))
                )
            )
        }
    }


    interface Service {
        @Multipart
        @POST(APIs.URL_BUSINESS_COP)
        fun submit(
            @Query("name") name: String,
            @Query("email") email: String,
            @Query("purpose") purpose: String,
            @Part cooperatefile: List<MultipartBody.Part>
        ): LiveData<Result<Boolean>>
    }
}