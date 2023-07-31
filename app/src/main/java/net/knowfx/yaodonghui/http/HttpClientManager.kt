package net.knowfx.yaodonghui.http

import android.annotation.SuppressLint
import android.content.Context
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.utils.NetConfig

class HttpClientManager {
    private var context: Context
    var client: RetrofitClient
        private set

    private constructor(context: Context) {
        this.context = context
        client = RetrofitClient(context, BuildConfig.SERVER_PATH + BuildConfig.CODE)
    }

    private constructor(context: Context, baseUrl: String) {
        this.context = context
        client = RetrofitClient(context, baseUrl)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: HttpClientManager
        fun getInstance(context: Context): HttpClientManager {
            if (!this::instance.isInitialized) {
                synchronized(HttpClientManager::class.java) {
                    instance = HttpClientManager(context)
                }
            }
            return instance
        }

        fun getInstance(context: Context, baseUrl: String): HttpClientManager {
            if (!this::instance.isInitialized) {
                synchronized(HttpClientManager::class.java) {
                    instance = HttpClientManager(context, baseUrl)
                }
            }
            return instance
        }
    }
}