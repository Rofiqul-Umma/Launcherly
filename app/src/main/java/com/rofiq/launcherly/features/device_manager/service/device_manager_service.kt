package com.rofiq.launcherly.features.device_manager.service

import android.content.Context
import android.content.Intent
import android.provider.Settings
import javax.inject.Inject

class DeviceManagerService @Inject constructor(val context: Context){

    fun openSystemSettings(){
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun openNetworkSettings(){
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}