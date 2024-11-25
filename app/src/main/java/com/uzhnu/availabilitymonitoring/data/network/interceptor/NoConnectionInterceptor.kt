package com.uzhnu.availabilitymonitoring.data.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoConnectionInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if (!isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }

    private fun isConnectionOn(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isConnectionOn(connectivityManager)
        } else {
            isConnectionLowerM(connectivityManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectionOn(connectivityManager: ConnectivityManager): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities?.let {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false

    }

    @Suppress("DEPRECATION")
    private fun isConnectionLowerM(connectivityManager: ConnectivityManager): Boolean {
        val activeNetworkInfo =
            connectivityManager.activeNetworkInfo

        return activeNetworkInfo?.isConnected ?: false
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val sock = Socket()
            val sockAddress = InetSocketAddress("8.8.8.8", PORT)

            sock.connect(sockAddress, TIMEOUT)
            sock.close()

            true
        } catch (e: IOException) {
            Log.e(TAG, e.stackTraceToString())
            false
        }
    }

    class NoConnectivityException : IOException() {
        override val message: String
            get() =
                "No network available, please check your WiFi or Data connection"
    }

    class NoInternetException : IOException() {
        override val message: String
            get() =
                "No internet available, please check your connected WIFi or Data"
    }

    private companion object {
        const val TAG = "Connection Interceptor"
        const val TIMEOUT = 2 * 1000
        const val PORT = 53
    }

}
