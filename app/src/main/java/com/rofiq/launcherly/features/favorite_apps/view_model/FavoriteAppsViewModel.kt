package com.rofiq.launcherly.features.favorite_apps.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.favorite_apps.model.FavoriteAppModel
import com.rofiq.launcherly.features.favorite_apps.service.FavoriteAppsService
import com.rofiq.launcherly.features.home.model.AppInfoModel
import com.rofiq.launcherly.features.home.service.HomeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavoriteAppsState

// Initial state
object FavoriteAppsInitialState : FavoriteAppsState

// Loading state
object FavoriteAppsLoadingState : FavoriteAppsState

// Loaded state
data class FavoriteAppsLoadedState(val apps: List<FavoriteAppModel>) : FavoriteAppsState

// Error state
data class FavoriteAppsErrorState(val message: String) : FavoriteAppsState

interface AllAppsState

// Initial state
object AllAppsInitialState : AllAppsState

// Loading state
object AllAppsLoadingState : AllAppsState

// Loaded state
data class AllAppsLoadedState(val apps: List<AppInfoModel>) : AllAppsState

// Error state
data class AllAppsErrorState(val message: String) : AllAppsState

@HiltViewModel
class FavoriteAppsViewModel @Inject constructor(
    private val favoriteAppsService: FavoriteAppsService,
    private val homeService: HomeService
) : ViewModel() {

    private val _favoriteAppsState = MutableStateFlow<FavoriteAppsState>(FavoriteAppsInitialState)
    val favoriteAppsState: StateFlow<FavoriteAppsState> = _favoriteAppsState.asStateFlow()

    private val _allAppsState = MutableStateFlow<AllAppsState>(AllAppsInitialState)
    val allAppsState: StateFlow<AllAppsState> = _allAppsState.asStateFlow()

    fun emitFavoriteAppsState(state: FavoriteAppsState) {
        viewModelScope.launch {
            _favoriteAppsState.emit(state)
        }
    }

    fun emitAllAppsState(state: AllAppsState) {
        viewModelScope.launch {
            _allAppsState.emit(state)
        }
    }

    fun fetchFavoriteApps() {
        viewModelScope.launch {
            try {
                val apps = favoriteAppsService.getFavoriteApps()
                emitFavoriteAppsState(FavoriteAppsLoadedState(apps))
            } catch (e: Exception) {
                emitFavoriteAppsState(FavoriteAppsErrorState(e.message ?: "Error fetching favorite apps"))
            }
        }
    }

    fun fetchAllApps() {
        viewModelScope.launch {
            emitAllAppsState(AllAppsLoadingState)
            try {
                val apps = homeService.fetchAllApps()
                emitAllAppsState(AllAppsLoadedState(apps))
            } catch (e: Exception) {
                emitAllAppsState(AllAppsErrorState(e.message ?: "Error fetching apps"))
            }
        }
    }

    fun addFavoriteApp(app: AppInfoModel) {
        viewModelScope.launch {
            try {
                val favoriteApp = FavoriteAppModel(app.packageName, app.name)
                favoriteAppsService.addFavoriteApp(favoriteApp)
                fetchFavoriteApps() // Refresh the favorite apps list
            } catch (e: Exception) {
                emitFavoriteAppsState(FavoriteAppsErrorState(e.message ?: "Error adding favorite app"))
            }
        }
    }

    fun removeFavoriteApp(packageName: String) {
        viewModelScope.launch {
            try {
                favoriteAppsService.removeFavoriteApp(packageName)
                fetchFavoriteApps() // Refresh the favorite apps list
            } catch (e: Exception) {
                emitFavoriteAppsState(FavoriteAppsErrorState(e.message ?: "Error removing favorite app"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}