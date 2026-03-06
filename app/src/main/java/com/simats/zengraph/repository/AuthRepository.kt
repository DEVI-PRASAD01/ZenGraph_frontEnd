package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return apiService.login(request)
    }

    suspend fun signup(request: SignupRequest): SignupResponse {
        return apiService.signup(request)
    }
}
