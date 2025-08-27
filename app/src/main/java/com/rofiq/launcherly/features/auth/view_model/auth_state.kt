package com.rofiq.launcherly.features.auth.view_model

import android.os.Message

interface AuthState

// initial state
object AuthInitial : AuthState

// loading state
object AuthLoading : AuthState

// authenticated state
object AuthAuthenticated : AuthState

// unauthenticated state
data class AuthUnauthenticated(val message: String) : AuthState
