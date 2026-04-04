package com.simats.zengraph.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.zengraph.network.LoginRequest
import com.simats.zengraph.network.LoginResponse
import com.simats.zengraph.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(request)
                // Success if we reached here as repo throws on failure
                _loginState.value = LoginState.Success(response)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}
