package com.rofiq.launcherly

import TimeChangeReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.rofiq.launcherly.features.app_change_receiver.receiver.AppChangeReceiver
import com.rofiq.launcherly.features.auth.view.LoginPage
import com.rofiq.launcherly.features.background_settings.view.BackgroundSettingsStep
import com.rofiq.launcherly.features.background_settings.view.TVFilePicker
import com.rofiq.launcherly.features.check_login.view.CheckLoginPage
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeViewModel
import com.rofiq.launcherly.features.guided_settings.view.GuidedSettingsStep
import com.rofiq.launcherly.features.home.view.HomePage
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var appReceiver: AppChangeReceiver? = null
    private var timeReceiver: TimeChangeReceiver? = null

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "check_login") {
                    composable("check_login") { CheckLoginPage(navController) }
                    composable("login") { LoginPage(navController = navController) }
                    composable("home") { HomePage(navController) }
                    composable("guided_settings") {
                        GuidedSettingsStep(
                            navController = navController
                        )
                    }
                    composable("background_settings") {
                        BackgroundSettingsStep(
                            navController
                        )
                    }
                    composable("local_file_picker") {
                        TVFilePicker(
                            navController
                        )
                    }
                }
            }
        }

        val fetchDateTimeVM: FetchDateTimeViewModel by viewModels()
        val homeVM: HomeViewModel by viewModels()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        timeReceiver = TimeChangeReceiver {
            fetchDateTimeVM.fetchCurrentDateTime()
        }
        // Register TimeChangeReceiver with its own filter for time-related actions
        val timeFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        registerReceiver(timeReceiver, timeFilter)

        appReceiver = AppChangeReceiver { homeVM.fetchInstalledApps() }

        registerReceiver(appReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(appReceiver)
        unregisterReceiver(timeReceiver)
    }
}
