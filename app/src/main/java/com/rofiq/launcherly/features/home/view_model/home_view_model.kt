package com.rofiq.launcherly.features.home.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.favorite_apps.service.FavoriteAppsService
import com.rofiq.launcherly.features.home.service.HomeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeService: HomeService,
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeInitialState)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        fetchFavoriteApps()
    }

    fun emit(state: HomeState) {
        viewModelScope.launch {
            _homeState.emit(state)
        }
    }

    fun fetchFavoriteApps() {
        viewModelScope.launch {
            try {
                val apps = homeService.fetchFavoriteApps()
                if (apps.isEmpty()) emit(HomeEmptyFetchAppsState)
                else emit(HomeLoadedFetchAppState(apps))
            } catch (e: Exception) {
                emit(HomeErrorFetchAppsState(e.message ?: "Error fetching apps"))
            }
        }
    }

    fun fetchAllApps() {
        viewModelScope.launch {
            try {
                val apps = homeService.fetchAllApps()
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
