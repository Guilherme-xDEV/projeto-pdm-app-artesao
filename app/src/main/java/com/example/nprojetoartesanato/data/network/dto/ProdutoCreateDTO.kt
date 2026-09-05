package com.example.nprojetoartesanato.data.network.dto

data class ProdutoCreateDTO(
    val nome: String,
    val descricao: String,
    val preco: Double,
    val quantidadeEstoque: Int,
    val qrCodeId: String
)
