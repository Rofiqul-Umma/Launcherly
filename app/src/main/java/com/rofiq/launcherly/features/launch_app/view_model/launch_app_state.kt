package com.rofiq.launcherly.features.launch_app.view_model

interface LaunchAppState

// initial state
object LaunchAppInitial : LaunchAppState

// loading state
data class LaunchAppLoading(val packageName: String) : LaunchAppState

// success state
object LaunchAppSuccess : LaunchAppState

// error state
data class LaunchAppError(val message: String) : LaunchAppState