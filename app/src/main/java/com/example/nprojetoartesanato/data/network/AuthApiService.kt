package com.example.nprojetoartesanato.data.network

import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.ArtesaoResponse
import com.example.nprojetoartesanato.data.network.dto.AuthResponse
import com.example.nprojetoartesanato.data.network.dto.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun signup(
        @Body dto: ArtesaoCreateDTO
    ): Response<ArtesaoResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}
