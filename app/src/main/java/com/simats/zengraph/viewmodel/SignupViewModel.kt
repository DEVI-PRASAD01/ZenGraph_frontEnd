package com.simats.zengraph.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.zengraph.network.SignupRequest
import com.simats.zengraph.network.SignupResponse
import com.simats.zengraph.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    data class Success(val response: SignupResponse) : SignupState()
    data class Error(val message: String) : SignupState()
}

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _signupState = MutableLiveData<SignupState>(SignupState.Idle)
    val signupState: LiveData<SignupState> = _signupState

    fun signup(request: SignupRequest) {
        viewModelScope.launch {
            _signupState.value = SignupState.Loading
            try {
                val response = repository.signup(request)
                _signupState.value = SignupState.Success(response)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    val jsonObject = com.google.gson.JsonObject()
                    val parsed = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    if (parsed.has("detail")) {
                        val detail = parsed.get("detail")
                        if (detail.isJsonArray) {
                            detail.asJsonArray[0].asJsonObject.get("msg").asString
                        } else {
                            detail.asString
                        }
                    } else if (parsed.has("message")) {
                        parsed.get("message").asString
                    } else {
                        "Validation error occurred"
                    }
                } catch (ex: Exception) {
                    "Error: ${e.code()}"
                }
                _signupState.value = SignupState.Error(message)
            } catch (e: Exception) {
                _signupState.value = SignupState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}
