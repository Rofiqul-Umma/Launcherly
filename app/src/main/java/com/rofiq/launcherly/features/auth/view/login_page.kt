package com.rofiq.launcherly.features.auth.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.CardNotification
import com.rofiq.launcherly.common.widgets.LoadingIndicator
import com.rofiq.launcherly.features.auth.view_model.AuthAuthenticated
import com.rofiq.launcherly.features.auth.view_model.AuthLoading
import com.rofiq.launcherly.features.auth.view_model.AuthUnauthenticated
import com.rofiq.launcherly.features.auth.view_model.AuthViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginPage(
    authVM: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val accessCodeFieldRequester = remember { FocusRequester() }
    val authState by authVM.authState.collectAsState()

    val accessCodeFieldInteractionSource = remember { MutableInteractionSource() }
    val isAccessCodeFieldFocused by accessCodeFieldInteractionSource.collectIsFocusedAsState()
    val snackbarHS = remember { SnackbarHostState() }

    val keyboardCL = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        accessCodeFieldRequester.requestFocus()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthAuthenticated -> {
                // User is authenticated, navigate to home
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    val textFieldScale by animateFloatAsState(
        targetValue = if (isAccessCodeFieldFocused) 1.05f else 1.0f,
        label = "TextField Scale"
    )

    Scaffold(
        containerColor = TVColors.Background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHS,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = TVColors.Border,
                        contentColor = TVColors.OnSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TVColors.OnSurface,
                            TVColors.BackgroundOverlay
                        ),
                        radius = 1200f
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_auth),
                contentDescription = "Login Image",
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = TVColors.Surface,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 64.dp, vertical = 64.dp)
                            .widthIn(max = 500.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sign in",
                            style = TVTypography.HeaderLarge.copy(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold

                            ),
                            color = TVColors.OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Enter your access code to continue",
                            style = TVTypography.BodyLarge.copy(
                                fontSize = 14.sp
                            ),
                            color = TVColors.OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(25.dp))


                        var username by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = username,
                            isError = authState is AuthAuthenticated,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    accessCodeFieldRequester.freeFocus()
                                    keyboardCL?.hide()
                                    authVM.signIn(username)
                                }),
                            onValueChange = { username = it },
                            placeholder = {
                                Text(
                                    text = "Access code",
                                    color = TVColors.OnSurfaceSecondary,
                                    style = TVTypography.BodyLarge
                                )
                            },
                            leadingIcon = {
                                when (authState) {
                                    is AuthLoading -> LoadingIndicator(size = 30)
                                    else -> Icon(
                                        Icons.Outlined.Lock,
                                        contentDescription = "Lock Icon",
                                        tint = Color.White
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .graphicsLayer(
                                    scaleX = textFieldScale,
                                    scaleY = textFieldScale
                                )
                                .width(320.dp)
                                .height(50.dp)
                                .focusRequester(accessCodeFieldRequester)
                                .focusable(), // Keep focusable for default behavior
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TVColors.Focused, // Updated focus color
                                unfocusedBorderColor = TVColors.Border,
                                focusedTextColor = TVColors.OnSurface,
                                unfocusedTextColor = TVColors.OnSurface,
                                cursorColor = TVColors.OnSurface,
                                focusedContainerColor = TVColors.InputBackground,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = TVTypography.BodyRegular.copy(
                                color = TVColors.OnSurface
                            ),
                            interactionSource = accessCodeFieldInteractionSource
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        if (authState is AuthUnauthenticated) {
                            CardNotification(
                                message = (authState as AuthUnauthenticated).message,
                                icon = Icons.Default.Error
                            )
                        }
                    }
                }
            }
        }
    }
}

