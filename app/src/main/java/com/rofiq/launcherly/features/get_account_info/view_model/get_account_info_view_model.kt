package com.rofiq.launcherly.features.get_account_info.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.get_account_info.service.GetAccountInfoService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GetAccountInfoViewModel @Inject constructor(
    val service: GetAccountInfoService
) : ViewModel() {

    private val _getAccountInfoState = MutableStateFlow<GetAccountInfoState>(GetAccountInfoInitial)
    val getAccountInfoState: StateFlow<GetAccountInfoState> = _getAccountInfoState.asStateFlow()

    init {
        getAccountGoogleInfo()
    }

    fun emit(state: GetAccountInfoState) {
       viewModelScope.launch {
           _getAccountInfoState.emit(state)
       }
    }

    fun getAccountGoogleInfo() {
        viewModelScope.launch {
            emit(GetAccountInfoLoading)
            try {
                val account = service.getLastSignedInGoogleAccount()
                if (account == null) {
                    emit(GetAccountInfoEmpty)
                    return@launch
                }
                emit(GetAccountInfoSuccess(account))
            } catch (e: Exception) {
                emit(GetAccountInfoError(e.message ?: "Error fetching account google"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}