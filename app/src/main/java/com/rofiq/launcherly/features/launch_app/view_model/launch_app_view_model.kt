package com.rofiq.launcherly.features.launch_app.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.launch_app.service.LaunchAppService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LaunchAppViewModel @Inject constructor(val service: LaunchAppService): ViewModel() {


    private val _launchAppState = MutableStateFlow<LaunchAppState>(LaunchAppInitial)
    val launchAppState: StateFlow<LaunchAppState> = _launchAppState.asStateFlow()

    fun emit(state: LaunchAppState) {
        viewModelScope.launch {
            _launchAppState.emit(state)
        }
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            Log.i("LAUNCH_APP", "Launching app with package name $packageName")
            try {
                emit(LaunchAppLoading(packageName))
                service.launchApp(packageName)
                emit(LaunchAppSuccess)
                // Reset to initial state after delay to allow UI updates
                delay(1.seconds)
                emit(LaunchAppInitial)
            } catch (e: Exception) {
                Log.e("LAUNCH_APP", "Error launching app: ${e.message}", e)
                emit(LaunchAppError(e.message ?: "Error launching app"))
                // Reset to initial state after delay on error too
                delay(3.seconds)
                emit(LaunchAppInitial)
            }
        }
    }
}