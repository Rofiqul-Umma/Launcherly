package com.rofiq.launcherly.features.device_manager.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.device_manager.service.DeviceManagerService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltViewModel
class DeviceManagerViewModel @Inject constructor(val service: DeviceManagerService): ViewModel(){


    fun openSystemSettings(){
        viewModelScope.launch {
            service.openSystemSettings()
        }
    }

    fun openNetworkSettings() {
        viewModelScope.launch {
            service.openNetworkSettings()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}