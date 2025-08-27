package com.rofiq.launcherly.features.app_change_receiver.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import javax.inject.Inject

class AppChangeReceiver(private val onRefreshApp: () -> Unit) : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {


        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> onRefreshApp()

            Intent.ACTION_PACKAGE_REMOVED -> onRefreshApp()

            Intent.ACTION_PACKAGE_REPLACED -> onRefreshApp()
        }
    }
}
