package net.knowfx.yaodonghui.http

import android.annotation.SuppressLint
import android.content.Context
import net.knowfx.yaodonghui.BuildConfig
import net.knowfx.yaodonghui.utils.NetConfig

class HttpClientManager {
    private var context: Context
    lateinit var client: RetrofitClient
    lateinit var client1: RetrofitClient

    private constructor (context: Context) {
        this.context = context
        client = RetrofitClient(context, BuildConfig.SERVER_PATH + BuildConfig.CODE)
    }

    private constructor(context: Context, baseUrl: String) {
        this.context = context
        client1 = RetrofitClient(context, baseUrl)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: HttpClientManager

        lateinit var instance1: HttpClientManager
        fun getInstance(context: Context): HttpClientManager {
            if (!this::instance.isInitialized) {
                synchronized(HttpClientManager::class.java) {
                    instance = HttpClientManager(context)
                }
            }
            return instance
        }

        fun getInstance(context: Context, baseUrl: String): HttpClientManager {
            if (!this::instance1.isInitialized) {
                synchronized(HttpClientManager::class.java) {
                    instance1 = HttpClientManager(context, baseUrl)
                }
            }
            return instance1
        }
    }
}