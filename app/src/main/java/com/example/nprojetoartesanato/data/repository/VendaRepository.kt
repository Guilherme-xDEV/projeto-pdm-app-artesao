package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.VendaRequest
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendaRepository {

    private val apiService = RetrofitClient.vendaApiService

    suspend fun registrarVendaRemote(produtoId: Long, quantidade: Int): Venda? {
        return try {
            val response = apiService.registrar(VendaRequest(produtoId, quantidade))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val venda = Venda(
                        id = body.id,
                        produto = body.produtoNome,
                        artesao = body.artesaoNome,
                        artesaoId = 0, // Não retornado diretamente no DTO simplificado, mas o app usa para filtragem local se necessário
                        vendedor = body.vendedorNome,
                        valor = "R$ %.2f".format(body.valorTotal),
                        quantidade = body.quantidade,
                        dataHora = body.dataHora
                    )
                    
                    // Atualiza estoque local para refletir a venda imediatamente
                    LocalDataStore.baixarEstoqueProduto(produtoId, quantidade)
                    LocalDataStore.adicionarVenda(venda)
                    venda
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sincronizarHistoricoRemote(): List<Venda> {
        return try {
            val response = apiService.listarMinhasVendas()
            if (response.isSuccessful) {
                val remoteList = response.body()?.map { body ->
                    Venda(
                        id = body.id,
                        produto = body.produtoNome,
                        artesao = body.artesaoNome,
                        artesaoId = 0,
                        vendedor = body.vendedorNome,
                        valor = "R$ %.2f".format(body.valorTotal),
                        quantidade = body.quantidade,
                        dataHora = body.dataHora
                    )
                } ?: emptyList()
                
                LocalDataStore.substituirVendas(remoteList)
                remoteList
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun registrarVenda(
        produto: Produto,
        vendedorNome: String,
        quantidade: Int = 1
    ): Venda? {

        val produtoAtualizado = LocalDataStore.baixarEstoqueProduto(produto.id, quantidade) ?: return null

        val nomeArtesao = LocalDataStore.buscarArtesaoPorId(produto.artesaoId)?.nome ?: "Desconhecido"

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        val venda = Venda(
            produto = produtoAtualizado.nome,
            artesao = nomeArtesao,
            artesaoId = produtoAtualizado.artesaoId,
            vendedor = vendedorNome,
            valor = "R$ %.2f".format(produtoAtualizado.preco * quantidade),
            quantidade = quantidade,
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
