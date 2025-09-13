package com.rofiq.launcherly.features.fetch_date_time.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.fetch_date_time.model.FetchDateTimeModel
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchDateTimeViewModel @Inject constructor(
    val service: FetchDateTimeService) : ViewModel() {

    private val _fetchDateTimeState = MutableStateFlow<FetchDateTimeState>(FetchDateTimeInitial)
    val fetchDateTimeState: StateFlow<FetchDateTimeState> = _fetchDateTimeState.asStateFlow()

    init {
        fetchCurrentDateTime()
    }

    fun emit(state: FetchDateTimeState){
        viewModelScope.launch {
            _fetchDateTimeState.emit(state)
        }
    }


    fun fetchCurrentDateTime(){
        viewModelScope.launch {
            try {
                val date = service.getCurrentDate()
                val time = service.getCurrentTime()
                emit(FetchDateTimeSuccess(FetchDateTimeModel(date, time)))
            } catch (e: Exception) {
                emit(FetchDateTimeFailed(e.message ?: "Error fetching date and time"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}