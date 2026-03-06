package com.simats.zengraph.repository

import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SignupRequest
import com.simats.zengraph.network.SignupResponse

class SignupRepository {
    suspend fun signup(request: SignupRequest): SignupResponse {
        return RetrofitClient.apiService.signup(request)
    }
}
