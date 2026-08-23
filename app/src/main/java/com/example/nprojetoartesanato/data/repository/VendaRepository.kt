package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendaRepository {

    fun registrarVenda(
        produto: Produto,
        vendedorNome: String
    ): Venda? {

        val produtoAtualizado = LocalDataStore.baixarEstoqueProduto(produto.id) ?: return null

        val nomeArtesao = LocalDataStore.buscarArtesaoPorId(produto.artesaoId)?.nome ?: "Desconhecido"

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        val venda = Venda(
            produto = produtoAtualizado.nome,
            artesao = nomeArtesao,
            vendedor = vendedorNome,
            valor = "R$ %.2f".format(produtoAtualizado.preco),
            dataHora = formato.format(Date())
        )

        return LocalDataStore.adicionarVenda(venda)
    }

    fun listarTodas(): List<Venda> {
        return LocalDataStore.listarVendas()
    }

    fun observarTodas(): Flow<List<Venda>> {
        return LocalDataStore.vendaList
    }
}