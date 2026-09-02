package com.example.nprojetoartesanato.data.network.dto

data class AuthResponse(
    val token: String,
    val artesaoId: Long,
    val nome: String
)
