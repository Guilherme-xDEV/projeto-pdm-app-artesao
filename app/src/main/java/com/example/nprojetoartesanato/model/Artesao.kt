package com.example.nprojetoartesanato.model

data class Artesao(
    val id: Long,
    val nome: String,
    val telefone: String,
    val identificacao: String,
    val usuario: String, // <-- authentication modeling will have this removed later.
    val senha: String
)
