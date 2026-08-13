package com.example.nprojetoartesanato.model

data class Produto(
    val id: Long,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val quantidadeEstoque: Int,
    val artesaoId: Long
)
