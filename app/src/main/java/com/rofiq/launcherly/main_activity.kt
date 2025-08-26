package com.rofiq.launcherly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.rofiq.launcherly.core.di.DaggerAppComponent
import com.rofiq.launcherly.features.auth.view.LoginPage
import com.rofiq.launcherly.features.home.view.HomePage
import com.rofiq.launcherly.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = DaggerAppComponent.create()
        appComponent.inject(this)


        setContent {
            MaterialTheme { // Apply MaterialTheme to the entire navigation structure
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginPage(navController) }
                    composable("home") {
                        HomePage()
                    }
                }
            }
        }
    }
}
