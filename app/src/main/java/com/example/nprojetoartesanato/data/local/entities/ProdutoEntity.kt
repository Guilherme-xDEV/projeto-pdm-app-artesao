package com.example.nprojetoartesanato.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nprojetoartesanato.model.Produto

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val quantidadeEstoque: Int,
    val artesaoId: Long,
    val qrCodeId: String,
    val ativo: Boolean = true
)

fun ProdutoEntity.toDomain() = Produto(
    id = id,
    nome = nome,
    descricao = descricao,
    preco = preco,
    quantidadeEstoque = quantidadeEstoque,
    artesaoId = artesaoId,
    qrCodeId = qrCodeId,
    ativo = ativo
)

fun Produto.toEntity() = ProdutoEntity(
    id = id,
    nome = nome,
    descricao = descricao,
    preco = preco,
    quantidadeEstoque = quantidadeEstoque,
    artesaoId = artesaoId,
    qrCodeId = qrCodeId,
    ativo = ativo
)
