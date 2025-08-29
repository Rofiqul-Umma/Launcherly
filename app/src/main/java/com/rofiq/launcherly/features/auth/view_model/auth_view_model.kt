package com.rofiq.launcherly.features.auth.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.auth.model.UserModel
import com.rofiq.launcherly.features.auth.service.AuthService
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeInitial
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val service: AuthService): ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthInitial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun emit(state: AuthState){
        viewModelScope.launch {
            _authState.emit(state)
        }
    }

    init {
        checkLogin()
    }

    fun checkLogin() {
        viewModelScope.launch {
            emit(AuthLoading)
            val response = service.checkLogin()
            if (response.isSuccess) emit(AuthAuthenticated)
            else emit(AuthUnauthenticated("Device unauthenticated, please register first to continue"))
        }
    }

    fun signIn(username: String) {
       viewModelScope.launch {
           try {
               emit(AuthLoading)

               if (username != "KURA1234") {
                   emit(AuthUnauthenticated("Invalid Access code"))
                   return@launch
               }

               if(username.trim().isEmpty()){
                   emit(AuthUnauthenticated("Access code cannot be empty"))
                   return@launch
               }

               val user = UserModel(username)
               val response = service.signIn(user)
               if (response.isSuccess) emit(AuthAuthenticated)
               else emit(AuthUnauthenticated("Device unauthenticated, please register first to continue"))
           } catch (e: Exception){
               emit(AuthUnauthenticated(e.message ?: "Error Sign In: Something went wrong"))
           }
       }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }
}