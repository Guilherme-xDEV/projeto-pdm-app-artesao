package com.example.nprojetoartesanato.model

data class Venda(
    val id: Long = 0,
    val produto: String,
    val artesao: String,
    val artesaoId: Long = 0,
    val vendedor: String,
    val valor: String,
    val dataHora: String
)
