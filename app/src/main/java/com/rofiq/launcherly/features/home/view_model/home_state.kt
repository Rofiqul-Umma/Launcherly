package com.rofiq.launcherly.features.home.view_model

import com.rofiq.launcherly.features.home.model.AppInfoModel

interface HomeState

// initial state
object HomeInitialState : HomeState

// loading fetch apps
object HomeLoadingFetchAppsState : HomeState

// fetch apps loaded
data class HomeLoadedFetchAppState(val apps: List<AppInfoModel>) : HomeState

// fetch apps empty
object HomeEmptyFetchAppsState : HomeState

// fetch apps error
data class HomeErrorFetchAppsState(val message: String) : HomeState
