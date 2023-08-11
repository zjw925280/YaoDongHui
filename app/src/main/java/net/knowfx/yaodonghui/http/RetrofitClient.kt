package net.knowfx.yaodonghui.http

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.ext.KEY_TOKEN
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.ext.readData
import net.knowfx.yaodonghui.utils.NetConfig
import net.knowfx.yaodonghui.utils.SSLSocketFactoryUtils.TrustAllHostnameVerifier
import net.knowfx.yaodonghui.utils.SSLSocketFactoryUtils.createSSLSocketFactory
import net.knowfx.yaodonghui.utils.SSLSocketFactoryUtils.createTrustAllManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetrofitClient(mContext: Context, baseUrl: String) {
    private val mRetrofit: Retrofit
    init {
        val socketFactory = createSSLSocketFactory(mContext)
        val trustManager = createTrustAllManager()
        val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (socketFactory != null && trustManager != null) {
            okHttpBuilder.sslSocketFactory(socketFactory, trustManager)
        }
        okHttpBuilder.hostnameVerifier(TrustAllHostnameVerifier())
            .addInterceptor(IsNullBodyInterceptor())
            .addInterceptor(AddHeaderInterceptor())
            .addInterceptor(LoggingInterceptor())
            .connectTimeout(NetConfig.API_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NetConfig.API_READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(NetConfig.API_WRITE_TIME_OUT, TimeUnit.SECONDS)

        Log.e("q请求","sss=="+baseUrl);
        val finalUrl =
            if (baseUrl.startsWith("http")) baseUrl else BuildConfig.SERVER_PATH + BuildConfig.CODE + baseUrl
        mRetrofit = Retrofit.Builder()
            .client(okHttpBuilder.build())
            .baseUrl(finalUrl) //设置网络请求的Url地址
            .addConverterFactory(GsonConverterFactory.create(Gson())) //设置数据解析器
            .addCallAdapterFactory(LiveDataCallAdapterFactory()) //设置请求响应适配 LiveData
            .build()
    }

    /**
     * 添加header包含cookie拦截器
     */
    inner class AddHeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder: Request.Builder = chain.request().newBuilder()
            builder.addHeader("apptype", "android")
            //添加用户登录认证
            val auth = readData(KEY_TOKEN, "")
            if (auth.isNotEmpty()) {
                builder.addHeader("Authorization", auth)
            }
            return chain.proceed(builder.build())
        }
    }

    /**
     * 判断返回body是否是null
     */
    inner class IsNullBodyInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val responseBody = response.body
            var content = responseBody!!.string()
            val mediaType = responseBody.contentType()
            if (content.isEmpty()) {
                content = Gson().toJson(System.currentTimeMillis().toString() + "")
            }
            return response.newBuilder().body(content.toResponseBody(mediaType)).build()
        }
    }

    inner class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val startTime = System.currentTimeMillis()
            val response: Response = chain.proceed(request)
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val responseBody = response.body ?: return response
            val mediaType = responseBody.contentType()
            val content = response.body!!.string()
            "==========================================请求打印===========================================".logE()
            "=请求地址：${request.url}".logE()
            "=headers: ${request.headers}".logE()
            ("=请求结果:  $content").logE()
            "==================================请求耗时:  $duration 毫秒===================================".logE()
            return response.newBuilder().body(content.toResponseBody(mediaType)).build()
        }
    }

    private class NullStringToEmptyAdapterFactory : TypeAdapterFactory {
        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val rawType = type.rawType as Class<T>
            return if (rawType != String::class.java) {
                null
            } else StringNullAdapter() as TypeAdapter<T>
        }
    }

    private class StringNullAdapter : TypeAdapter<String?>() {
        @Throws(IOException::class)
        override fun read(reader: JsonReader): String? {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return ""
            }
            return reader.nextString()
        }

        @Throws(IOException::class)
        override fun write(writer: JsonWriter, value: String?) {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        }
    }

    fun <T> createService(service: Class<T>?): T {
        return mRetrofit.create(service)
    }
}