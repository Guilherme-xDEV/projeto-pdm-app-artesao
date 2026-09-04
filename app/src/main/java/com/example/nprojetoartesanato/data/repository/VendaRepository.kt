package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.dao.VendaDao
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.VendaRequest
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendaRepository(
    private val vendaDao: VendaDao,
    private val produtoDao: ProdutoDao
) {

    private val apiService = RetrofitClient.vendaApiService

    suspend fun registrarVendaRemote(produtoId: Long, quantidade: Int): Venda? {
        return try {
            val response = apiService.registrar(VendaRequest(produtoId, quantidade))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val artesaoId = com.example.nprojetoartesanato.data.session.SessionManager.artesaoAtual.value?.id ?: 0
                    
                    val venda = Venda(
                        id = body.id,
                        produto = body.produtoNome,
                        artesao = body.artesaoNome,
                        artesaoId = artesaoId,
                        vendedor = body.vendedorNome,
                        valor = "R$ %.2f".format(body.valorTotal),
                        quantidade = body.quantidade,
                        dataHora = body.dataHora
                    )
                    
                    // Update stock locally
                    val produtoEntity = produtoDao.getById(produtoId)
                    if (produtoEntity != null) {
                        val updated = produtoEntity.copy(
                            quantidadeEstoque = (produtoEntity.quantidadeEstoque - quantidade).coerceAtLeast(0)
                        )
                        produtoDao.update(updated)
                    }
                    
                    vendaDao.insert(venda.toEntity())
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
                val artesaoId = com.example.nprojetoartesanato.data.session.SessionManager.artesaoAtual.value?.id ?: 0
                val remoteList = response.body()?.map { body ->
                    Venda(
                        id = body.id,
                        produto = body.produtoNome,
                        artesao = body.artesaoNome,
                        artesaoId = artesaoId,
                        vendedor = body.vendedorNome,
                        valor = "R$ %.2f".format(body.valorTotal),
                        quantidade = body.quantidade,
                        dataHora = body.dataHora
                    )
                } ?: emptyList()
                
                vendaDao.deleteAll()
                vendaDao.insertAll(remoteList.map { it.toEntity() })
                remoteList
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun registrarVenda(
        produto: Produto,
        vendedorNome: String,
        quantidade: Int = 1
    ): Venda? {
        // Local only fallback (mostly unused now but kept for consistency)
        val entity = produtoDao.getById(produto.id) ?: return null
        val updated = entity.copy(
            quantidadeEstoque = (entity.quantidadeEstoque - quantidade).coerceAtLeast(0)
        )
        produtoDao.update(updated)

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        val venda = Venda(
            id = 0, // Local only
            produto = updated.nome,
            artesao = "Local",
            artesaoId = updated.artesaoId,
            vendedor = vendedorNome,
            valor = "R$ %.2f".format(updated.preco * quantidade),
            quantidade = quantidade,
            dataHora = formato.format(Date())
        )

        vendaDao.insert(venda.toEntity())
        return venda
    }

    fun observarTodas(): Flow<List<Venda>> {
        return vendaDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
