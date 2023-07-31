package net.knowfx.yaodonghui.utils

import android.content.Context
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

object SSLHelper {
    private const val KEY_STORE_TYPE_BKS = "bks"
    private const val KEY_STORE_TYPE_P12 = "PKCS12"
    const val KEY_STORE_CLIENT_PATH = "server.pfx" //P12文件
    private const val KEY_STORE_TRUST_PATH = "client.truststore" //truststore文件
    const val KEY_STORE_PASSWORD = "aV8aXA5X" //P12文件密码
    private const val KEY_STORE_TRUST_PASSWORD = "123456" //truststore文件密码
    fun getSSLSocketFactory(context: Context): SSLSocketFactory? {
        var factory: SSLSocketFactory? = null
        try {
            // 服务器端需要验证的客户端证书
            val keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12)
            // 客户端信任的服务器端证书
            val trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS)
            val ksIn = context.resources.assets
                .open(KEY_STORE_CLIENT_PATH)
            val tsIn = context.resources.assets
                .open(KEY_STORE_TRUST_PATH)
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray())
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    ksIn.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    tsIn.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //信任管理器
            val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            )
            trustManagerFactory.init(trustStore)
            //密钥管理器
            val keyManagerFactory = KeyManagerFactory.getInstance("X509")
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray())
            //初始化SSLContext
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(
                keyManagerFactory.keyManagers,
                trustManagerFactory.trustManagers, null
            )
            factory = sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        }
        return factory
    }

    class UnSafeHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }
}