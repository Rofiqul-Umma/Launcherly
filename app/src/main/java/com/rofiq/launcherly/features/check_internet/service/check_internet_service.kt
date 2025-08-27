package com.rofiq.launcherly.features.check_internet.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class CheckInternetService @Inject constructor(private val context: Context) {

    fun registerInternetCallback(onStatusChanged: (Boolean) -> Unit) {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build()

            connectivityManager.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {

                    override fun onAvailable(network: Network) {
                        onStatusChanged(true)
                    }

                    override fun onLost(network: Network) {
                        onStatusChanged(false)
                    }

                    override fun onUnavailable() {
                        onStatusChanged(false)
                    }

                    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                        onStatusChanged(!blocked)
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}