package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.ProdutoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.ProdutoUpdateDTO
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutoRepository(private val produtoDao: ProdutoDao) {

    private val apiService = RetrofitClient.produtoApiService

    suspend fun cadastrarRemote(dto: ProdutoCreateDTO): Produto? {
        return try {
            val response = apiService.criar(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val produto = mapResponseToModel(body)
                    produtoDao.insert(produto.toEntity())
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
                    // Replace locally
                    produtoDao.deleteByArtesao(artesaoId)
                    produtoDao.insertAll(remoteList.map { it.toEntity() })
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
                val body = response.body()
                if (body != null) {
                    val produto = mapResponseToModel(body)
                    produtoDao.update(produto.toEntity())
                    produto
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletarRemote(id: Long): Boolean {
        return try {
            val response = apiService.deletar(id)
            if (response.isSuccessful) {
                produtoDao.deleteById(id)
                true
            } else false
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

    suspend fun cadastrar(produto: Produto): Produto {
        produtoDao.insert(produto.toEntity())
        return produto
    }

    fun observarPorArtesao(artesaoId: Long): Flow<List<Produto>> {
        return produtoDao.getByArtesao(artesaoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun buscarPorId(id: Long): Produto? {
        return produtoDao.getById(id)?.toDomain()
    }

    suspend fun listarTodos(): List<Produto> {
        return produtoDao.getAllSync().map { it.toDomain() }
    }

    suspend fun atualizar(id: Long, dados: AtualizarProdutoDTO): Produto? {
        val entity = produtoDao.getById(id) ?: return null
        val updated = entity.copy(
            nome = dados.nome,
            descricao = dados.descricao,
            preco = dados.preco
        )
        produtoDao.update(updated)
        return updated.toDomain()
    }

    suspend fun excluir(id: Long): Boolean {
        produtoDao.deleteById(id)
        return true
    }

    suspend fun baixarEstoque(id: Long, quantidade: Int): Boolean {
        val entity = produtoDao.getById(id) ?: return false
        val updated = entity.copy(
            quantidadeEstoque = (entity.quantidadeEstoque - quantidade).coerceAtLeast(0)
        )
        produtoDao.update(updated)
        return true
    }
}
