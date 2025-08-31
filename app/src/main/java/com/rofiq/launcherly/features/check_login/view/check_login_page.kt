package com.rofiq.launcherly.features.check_login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.auth.view_model.AuthAuthenticated
import com.rofiq.launcherly.features.auth.view_model.AuthLoading
import com.rofiq.launcherly.features.auth.view_model.AuthUnauthenticated
import com.rofiq.launcherly.features.auth.view_model.AuthViewModel

@Composable
fun CheckLoginPage(
    navController: NavController,
    authVM: AuthViewModel = hiltViewModel()
) {
    val authState by authVM.authState.collectAsState()
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthLoading -> {
                // Still checking authentication status
            }
            is AuthAuthenticated -> {
                // User is authenticated, navigate to home
                navController.navigate("home") {
                    popUpTo("check_login") { inclusive = true }
                }
            }
            is AuthUnauthenticated -> {
                // User is not authenticated, navigate to login
                navController.navigate("login") {
                    popUpTo("check_login") { inclusive = true }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TVColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo or icon
//            Image(
//                painter = painterResource(id = R.drawable.background_auth), // Replace with your app logo
//                contentDescription = "App Logo",
//                modifier = Modifier.size(120.dp)
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
            
            // App name
            Text(
                text = "Check Authentication Status",
                style = TVTypography.HeaderLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TVColors.OnSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Loading text
            Text(
                text = "Please wait while we check your authentication status",
                style = TVTypography.BodyLarge,
                color = TVColors.OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading indicator
            LCircularLoading(
                color = TVColors.OnSurface,
                size = 30,
                strokeWidth = 4
            )
        }
    }
}