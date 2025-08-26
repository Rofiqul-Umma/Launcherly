package com.rofiq.launcherly.features.check_internet.view_model

interface CheckInternetState

// Initial state
object CheckInternetInitial : CheckInternetState

// Loading state
object CheckInternetLoading : CheckInternetState

// IsConnected state
object CheckInternetIsConnected : CheckInternetState

// IsNotConnected state
object CheckInternetIsNotConnected : CheckInternetState

// Error state
data class CheckInternetError(val error: String) : CheckInternetState