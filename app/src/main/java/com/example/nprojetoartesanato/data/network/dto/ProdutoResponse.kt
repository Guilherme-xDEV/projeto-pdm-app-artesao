package com.example.nprojetoartesanato.data.network.dto

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val quantidadeEstoque: Int,
    val qrCodeId: String,
    val artesaoId: Long
)
