package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.ProdutoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.ProdutoUpdateDTO
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutoRepository {

    private val apiService = RetrofitClient.produtoApiService

    suspend fun cadastrarRemote(dto: ProdutoCreateDTO): Produto? {
        return try {
            val response = apiService.criar(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val produto = mapResponseToModel(body)
                    LocalDataStore.adicionarProduto(produto)
                    produto
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun listarMeusProdutosRemote(): List<Produto> {
        return try {
            val response = apiService.listarMeusProdutos()
            if (response.isSuccessful) {
                val remoteList = response.body()?.map { mapResponseToModel(it) } ?: emptyList()
                com.example.nprojetoartesanato.data.session.SessionManager.artesaoAtual.value?.id?.let { artesaoId ->
                    LocalDataStore.substituirProdutosPorArtesao(artesaoId, remoteList)
                }
                remoteList
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarPorIdRemote(id: Long): Produto? {
        return try {
            val response = apiService.buscarPorId(id)
            if (response.isSuccessful) {
                response.body()?.let { mapResponseToModel(it) }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun atualizarRemote(id: Long, dto: ProdutoUpdateDTO): Produto? {
        return try {
            val response = apiService.atualizar(id, dto)
            if (response.isSuccessful) {
                response.body()?.let { mapResponseToModel(it) }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletarRemote(id: Long): Boolean {
        return try {
            val response = apiService.deletar(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    private fun mapResponseToModel(response: com.example.nprojetoartesanato.data.network.dto.ProdutoResponse): Produto {
        return Produto(
            id = response.id,
            nome = response.nome,
            descricao = response.descricao,
            preco = response.preco,
            quantidadeEstoque = response.quantidadeEstoque,
            artesaoId = response.artesaoId,
            qrCodeId = response.qrCodeId
        )
    }

    fun cadastrar(
        produto: Produto,
    ): Produto {

        return LocalDataStore.adicionarProduto(produto)
    }

    fun buscarPorArtesao(
        artesaoId: Long
    ): List<Produto> {

        return LocalDataStore.buscarProdutosDoArtesao(
            artesaoId
        )
    } // <-- this returns List<Produto>

    fun observarPorArtesao(
        artesaoId: Long
    ): Flow<List<Produto>> {

        return LocalDataStore.produtoList.map { produtos ->
            produtos.filter {
                it.artesaoId == artesaoId
            }
        }
    } // <-- this returns Flow<List<Produto>>

    fun buscarPorId(
        id: Long
    ): Produto? {

        return LocalDataStore.buscarProdutoPorId(id)
    }

    fun listarTodos(): List<Produto> {
        return LocalDataStore.listarProdutos()
    }

    fun atualizar(
        id: Long,
        dados: AtualizarProdutoDTO
    ): Produto? {

        return LocalDataStore.atualizarProduto(
            id,
            dados
        )
    }

    fun excluir(
        id: Long
    ): Boolean{
        return LocalDataStore.excluirProduto(id)
    }
}