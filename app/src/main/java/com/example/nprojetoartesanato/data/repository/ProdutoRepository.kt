package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.firebase.FirebaseClient
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.network.dto.ProdutoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.ProdutoUpdateDTO
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProdutoRepository(private val produtoDao: ProdutoDao) {

    suspend fun cadastrarRemote(dto: ProdutoCreateDTO): Produto? {
        return try {
            val artesaoId = SessionManager.artesaoAtual.value?.id ?: 0L
            val docRef = FirebaseClient.produtosCollection.document()
            val numericId = (docRef.id.hashCode().toLong() and 0x7FFFFFFF)

            val produtoData = hashMapOf(
                "id" to numericId,
                "firestoreId" to docRef.id,
                "nome" to dto.nome,
                "descricao" to dto.descricao,
                "preco" to dto.preco,
                "quantidadeEstoque" to dto.quantidadeEstoque,
                "artesaoId" to artesaoId,
                "qrCodeId" to dto.qrCodeId,
                "ativo" to true
            )

            docRef.set(produtoData).await()

            val produto = Produto(
                id = numericId,
                nome = dto.nome,
                descricao = dto.descricao,
                preco = dto.preco,
                quantidadeEstoque = dto.quantidadeEstoque,
                artesaoId = artesaoId,
                qrCodeId = dto.qrCodeId,
                ativo = true
            )

            produtoDao.insert(produto.toEntity())
            produto
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun listarMeusProdutosRemote(): List<Produto> {
        return try {
            val artesaoId = SessionManager.artesaoAtual.value?.id ?: 0L
            val querySnapshot = FirebaseClient.produtosCollection
                .whereEqualTo("artesaoId", artesaoId)
                .whereEqualTo("ativo", true)
                .get()
                .await()

            val remoteList = querySnapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id") ?: return@mapNotNull null
                val nome = doc.getString("nome") ?: ""
                val descricao = doc.getString("descricao") ?: ""
                val preco = doc.getDouble("preco") ?: 0.0
                val quantidadeEstoque = doc.getLong("quantidadeEstoque")?.toInt() ?: 0
                val qrCodeId = doc.getString("qrCodeId") ?: ""
                val ativo = doc.getBoolean("ativo") ?: true

                Produto(
                    id = id,
                    nome = nome,
                    descricao = descricao,
                    preco = preco,
                    quantidadeEstoque = quantidadeEstoque,
                    artesaoId = artesaoId,
                    qrCodeId = qrCodeId,
                    ativo = ativo
                )
            }

            if (artesaoId != 0L) {
                produtoDao.deleteByArtesao(artesaoId)
                produtoDao.insertAll(remoteList.map { it.toEntity() })
            }

            remoteList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun buscarPorIdRemote(id: Long): Produto? {
        return try {
            val querySnapshot = FirebaseClient.produtosCollection
                .whereEqualTo("id", id)
                .get()
                .await()

            val doc = querySnapshot.documents.firstOrNull() ?: return null
            val artesaoId = doc.getLong("artesaoId") ?: 0L
            val nome = doc.getString("nome") ?: ""
            val descricao = doc.getString("descricao") ?: ""
            val preco = doc.getDouble("preco") ?: 0.0
            val quantidadeEstoque = doc.getLong("quantidadeEstoque")?.toInt() ?: 0
            val qrCodeId = doc.getString("qrCodeId") ?: ""
            val ativo = doc.getBoolean("ativo") ?: true

            Produto(
                id = id,
                nome = nome,
                descricao = descricao,
                preco = preco,
                quantidadeEstoque = quantidadeEstoque,
                artesaoId = artesaoId,
                qrCodeId = qrCodeId,
                ativo = ativo
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun atualizarRemote(id: Long, dto: ProdutoUpdateDTO): Produto? {
        return try {
            val querySnapshot = FirebaseClient.produtosCollection
                .whereEqualTo("id", id)
                .get()
                .await()

            val doc = querySnapshot.documents.firstOrNull() ?: return null
            val updates = mutableMapOf<String, Any>()

            dto.nome?.let { updates["nome"] = it }
            dto.descricao?.let { updates["descricao"] = it }
            dto.preco?.let { updates["preco"] = it }

            if (updates.isNotEmpty()) {
                doc.reference.update(updates).await()
            }

            val existing = produtoDao.getById(id)
            if (existing != null) {
                val updatedEntity = existing.copy(
                    nome = dto.nome ?: existing.nome,
                    descricao = dto.descricao ?: existing.descricao,
                    preco = dto.preco ?: existing.preco
                )
                produtoDao.update(updatedEntity)
                updatedEntity.toDomain()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deletarRemote(id: Long): Boolean {
        return try {
            val querySnapshot = FirebaseClient.produtosCollection
                .whereEqualTo("id", id)
                .get()
                .await()

            val doc = querySnapshot.documents.firstOrNull() ?: return false
            doc.reference.update("ativo", false).await()
            produtoDao.deleteById(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
