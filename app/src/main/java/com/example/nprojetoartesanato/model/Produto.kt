package com.example.nprojetoartesanato.model

data class Produto(
    // Local Room key retained for the existing navigation and UI.
    val id: Long,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val quantidadeEstoque: Int,
    val artesaoId: Long,
    val qrCodeId: String,
    val ativo: Boolean = true,
    // Firestore document ID is the remote identity used for every cloud operation.
    val firestoreId: String = ""
)
