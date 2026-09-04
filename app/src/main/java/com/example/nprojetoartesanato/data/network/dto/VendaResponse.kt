package com.example.nprojetoartesanato.data.network.dto

data class VendaResponse(
    val id: Long,
    val produtoNome: String,
    val valorTotal: Double,
    val quantidade: Int,
    val dataHora: String,
    val artesaoNome: String,
    val vendedorNome: String
)
