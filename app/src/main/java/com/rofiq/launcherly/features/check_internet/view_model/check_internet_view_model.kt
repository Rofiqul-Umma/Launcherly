package com.rofiq.launcherly.features.check_internet.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.check_internet.service.CheckInternetService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class CheckInternetViewModel @Inject constructor(
    val service: CheckInternetService) : ViewModel() {

    private val _checkInternetState = MutableStateFlow<CheckInternetState>(CheckInternetInitial)
    val checkInternetState: StateFlow<CheckInternetState> = _checkInternetState.asStateFlow()

    init {
        checkInternet()
    }

    fun emit(state: CheckInternetState) {
        viewModelScope.launch {
            _checkInternetState.emit(state)
        }
    }

    fun checkInternet() {
        viewModelScope.launch {
            try {
                emit(CheckInternetLoading)
                service.registerInternetCallback { isConnected ->
                    if (isConnected) {
                        emit(CheckInternetIsConnected)
                    } else {
                        emit(CheckInternetIsNotConnected)
                    }
                }
            } catch (e: Exception) {
                emit(CheckInternetError(e.message ?: "Failed to check internet connection"))
            }
        }
    }
}