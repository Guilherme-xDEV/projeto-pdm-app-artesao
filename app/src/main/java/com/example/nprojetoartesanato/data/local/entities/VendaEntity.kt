package com.example.nprojetoartesanato.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nprojetoartesanato.model.Venda

@Entity(tableName = "vendas")
data class VendaEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Long,
    val produto: String,
    val artesao: String,
    val artesaoId: Long,
    val vendedor: String,
    val valor: String,
    val quantidade: Int,
    val dataHora: String
)

fun VendaEntity.toDomain() = Venda(
    id = remoteId,
    produto = produto,
    artesao = artesao,
    artesaoId = artesaoId,
    vendedor = vendedor,
    valor = valor,
    quantidade = quantidade,
    dataHora = dataHora
)

fun Venda.toEntity() = VendaEntity(
    remoteId = id,
    produto = produto,
    artesao = artesao,
    artesaoId = artesaoId,
    vendedor = vendedor,
    valor = valor,
    quantidade = quantidade,
    dataHora = dataHora
)
