package com.rofiq.launcherly.features.home.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.home.service.HomeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeService: HomeService
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeInitialState)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                fetchInstalledApps()
                delay(1.minutes)
            }
        }
    }

    fun emit(state: HomeState) {
        viewModelScope.launch {
            _homeState.emit(state)
        }
    }

    fun fetchInstalledApps() {
        viewModelScope.launch {
            try {
                val apps = homeService.fetchInstalledApps()
                if (apps.isEmpty()) emit(HomeEmptyFetchAppsState)
                else emit(HomeLoadedFetchAppState(apps))
            } catch (e: Exception) {
                emit(HomeErrorFetchAppsState(e.message ?: "Error fetching apps"))
            }
        }
}

override fun onCleared() {
    super.onCleared()
    viewModelScope.coroutineContext.cancel()
}
}
