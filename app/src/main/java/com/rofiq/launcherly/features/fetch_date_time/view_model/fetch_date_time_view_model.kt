package com.rofiq.launcherly.features.fetch_date_time.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.fetch_date_time.model.FetchDateTimeModel
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class FetchDateTimeViewModel @Inject constructor(
    val service: FetchDateTimeService) : ViewModel() {

    private val _fetchDateTimeState = MutableStateFlow<FetchDateTimeState>(FetchDateTimeInitial)
    val fetchDateTimeState: StateFlow<FetchDateTimeState> = _fetchDateTimeState.asStateFlow()

    init {
        startDateTimeTicker()
    }

    fun emit(state: FetchDateTimeState){
        viewModelScope.launch {
            _fetchDateTimeState.emit(state)
        }
    }


    fun fetchCurrentDateTime(){
        viewModelScope.launch {
            updateCurrentDateTime()
        }
    }

    private fun startDateTimeTicker() {
        viewModelScope.launch {
            while (true) {
                updateCurrentDateTime()
                // Align ticks to the wall-clock second boundary so the displayed
                // seconds stay in sync with real time (avoids drift + stale second).
                val now = System.currentTimeMillis()
                delay((1000 - (now % 1000)).milliseconds)
            }
        }
    }

    private suspend fun updateCurrentDateTime() {
        try {
            val date = service.getCurrentDate()
            val time = service.getCurrentTime()
            _fetchDateTimeState.emit(FetchDateTimeSuccess(FetchDateTimeModel(date, time)))
        } catch (e: Exception) {
            _fetchDateTimeState.emit(FetchDateTimeFailed(e.message ?: "Error fetching date and time"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}
