package com.rofiq.launcherly.features.auth.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography

@Composable
fun LoginPage(
    navController: NavController
) {
    val accessCodeFieldRequester = remember { FocusRequester() }
    val signInButtonRequester = remember { FocusRequester() }

    val accessCodeFieldInteractionSource = remember { MutableInteractionSource() }
    val isAccessCodeFieldFocused by accessCodeFieldInteractionSource.collectIsFocusedAsState()

    val signInButtonInteractionSource = remember { MutableInteractionSource() }
    val isSignInButtonFocused by signInButtonInteractionSource.collectIsFocusedAsState()

    LaunchedEffect(Unit) {
        accessCodeFieldRequester.requestFocus()
    }

    val buttonScale by animateFloatAsState(targetValue = if (isSignInButtonFocused) 1.1f else 1.0f, label = "Button Scale")
    val textFieldScale by animateFloatAsState(targetValue = if (isAccessCodeFieldFocused) 1.05f else 1.0f, label = "TextField Scale")

    Scaffold(
        containerColor = TVColors.Background
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
                // Main Card - VLEPO style but in grey
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

                        // Access Code Input - VLEPO style
                        val accessCode = remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = accessCode.value,
                            keyboardActions = KeyboardActions(
                                onDone = { navController.navigate("home") }),
                            onValueChange = { accessCode.value = it },
                            placeholder = {
                                Text(
                                    text = "Access code",
                                    color = TVColors.OnSurfaceSecondary,
                                    style = TVTypography.BodyLarge
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = "Lock Icon",
                                    tint = Color.White
                                )
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
                            textStyle = TVTypography.BodyLarge,
                            interactionSource = accessCodeFieldInteractionSource
                        )
                    }
                }
            }
        }
    }
}

