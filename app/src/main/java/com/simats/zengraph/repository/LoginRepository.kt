package com.simats.zengraph.repository

import com.simats.zengraph.network.LoginRequest
import com.simats.zengraph.network.LoginResponse
import com.simats.zengraph.network.RetrofitClient

class LoginRepository {
    suspend fun login(request: LoginRequest): LoginResponse {
        return RetrofitClient.apiService.login(request)
    }
}
