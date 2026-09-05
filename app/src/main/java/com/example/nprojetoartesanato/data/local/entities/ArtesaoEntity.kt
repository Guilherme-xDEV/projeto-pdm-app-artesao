package com.example.nprojetoartesanato.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nprojetoartesanato.model.Artesao

@Entity(tableName = "artesaos")
data class ArtesaoEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val telefone: String,
    val identificacao: String,
    val email: String,
    val senha: String
)

fun ArtesaoEntity.toDomain() = Artesao(
    id = id,
    nome = nome,
    telefone = telefone,
    identificacao = identificacao,
    email = email,
    senha = senha
)

fun Artesao.toEntity() = ArtesaoEntity(
    id = id,
    nome = nome,
    telefone = telefone,
    identificacao = identificacao,
    email = email,
    senha = senha
)
