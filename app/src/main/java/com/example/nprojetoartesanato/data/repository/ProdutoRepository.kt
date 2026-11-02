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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProdutoRepository(private val produtoDao: ProdutoDao) {

    private fun idLocal(documentId: String): Long =
        (documentId.hashCode().toLong() and 0x7FFFFFFF)

    private fun precoEmReais(document: DocumentSnapshot): Double {
        val centavos = (document.get("precoCentavos") as? Number)?.toLong()
        if (centavos != null) return centavos / 100.0
        return (document.get("preco") as? Number)?.toDouble() ?: 0.0
    }

    private fun produtoDoDocumento(
        document: DocumentSnapshot,
        artesaoIdLocal: Long
    ): Produto {
        val id = document.getLong("id") ?: idLocal(document.id)
        val quantidade = (document.get("quantidadeEstoque") as? Number)?.toInt() ?: 0

        return Produto(
            id = id,
            nome = document.getString("nome") ?: "",
            descricao = document.getString("descricao") ?: "",
            preco = precoEmReais(document),
            quantidadeEstoque = quantidade,
            artesaoId = artesaoIdLocal,
            qrCodeId = document.getString("qrCodeId") ?: document.id,
            ativo = document.getBoolean("ativo") ?: true,
            firestoreId = document.id
        )
    }

    /**
     * Creates canonical product documents:
     * - Firestore document ID equals qrCodeId.
     * - artesaoId is the Firebase Auth UID.
     * - price is stored as an integer number of cents.
     */
    suspend fun cadastrarRemote(dto: ProdutoCreateDTO): Produto? {
        return try {
            val artesao = SessionManager.artesaoAtual.value ?: return null
            val uid = FirebaseClient.auth.currentUser?.uid ?: return null
            val docRef = FirebaseClient.produtosCollection.document(dto.qrCodeId)
            val numericId = idLocal(docRef.id)
            val precoCentavos = kotlin.math.round(dto.preco * 100).toLong()

            val produtoData = hashMapOf<String, Any>(
                "id" to numericId,
                "firestoreId" to docRef.id,
                "nome" to dto.nome,
                "descricao" to dto.descricao,
                "precoCentavos" to precoCentavos,
                "quantidadeEstoque" to dto.quantidadeEstoque,
                "artesaoId" to uid,
                "artesaoNome" to artesao.nome,
                "qrCodeId" to dto.qrCodeId,
                "ativo" to true,
                "criadoEm" to FieldValue.serverTimestamp()
            )

            FirebaseClient.db.runTransaction { transaction ->
                if (transaction.get(docRef).exists()) {
                    throw IllegalStateException("Identificador QR já utilizado. Gere outro código.")
                }
                transaction.set(docRef, produtoData)
                null
            }.await()

            val produto = Produto(
                id = numericId,
                nome = dto.nome,
                descricao = dto.descricao,
                preco = precoCentavos / 100.0,
                quantidadeEstoque = dto.quantidadeEstoque,
                artesaoId = artesao.id,
                qrCodeId = dto.qrCodeId,
                ativo = true,
                firestoreId = docRef.id
            )

            produtoDao.insert(produto.toEntity())
            produto
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads both canonical UID-owned documents and legacy documents whose
     * artesaoId is the numeric Room/profile ID. Legacy documents are upgraded
     * in place; their document ID and QR payload are never changed.
     */
    suspend fun listarMeusProdutosRemote(): List<Produto> {
        return try {
            val artesao = SessionManager.artesaoAtual.value ?: return emptyList()
            val uid = FirebaseClient.auth.currentUser?.uid ?: return emptyList()
            val produtos = FirebaseClient.produtosCollection

            val canonicos = produtos
                .whereEqualTo("artesaoId", uid)
                .whereEqualTo("ativo", true)
                .get()
                .await()

            val legados = produtos
                .whereEqualTo("artesaoId", artesao.id)
                .whereEqualTo("ativo", true)
                .get()
                .await()

            val documentos = (canonicos.documents + legados.documents).distinctBy { it.id }

            // Backfill metadata on documents created by the first web version.
            canonicos.documents.forEach { document ->
                try {
                    val updates = mutableMapOf<String, Any>()
                    if (document.getLong("id") == null) {
                        updates["id"] = idLocal(document.id)
                    }
                    if (document.getString("firestoreId") != document.id) {
                        updates["firestoreId"] = document.id
                    }
                    if (document.getString("artesaoNome").isNullOrBlank()) {
                        updates["artesaoNome"] = artesao.nome
                    }
                    if (updates.isNotEmpty()) {
                        updates["atualizadoEm"] = FieldValue.serverTimestamp()
                        document.reference.update(updates).await()
                    }
                } catch (migrationError: Exception) {
                    migrationError.printStackTrace()
                }
            }

            // Migrate legacy documents after the query, retaining their stable document IDs.
            legados.documents.forEach { document ->
                try {
                    val precoCentavos = kotlin.math.round(precoEmReais(document) * 100).toLong()
                    if (precoCentavos > 0) {
                        document.reference.update(
                            mapOf(
                                "id" to (document.getLong("id") ?: idLocal(document.id)),
                                "firestoreId" to document.id,
                                "artesaoId" to uid,
                                "artesaoNome" to (document.getString("artesaoNome") ?: artesao.nome),
                                "qrCodeId" to (document.getString("qrCodeId") ?: document.id),
                                "precoCentavos" to precoCentavos,
                                "preco" to FieldValue.delete(),
                                "atualizadoEm" to FieldValue.serverTimestamp()
                            )
                        ).await()
                    }
                } catch (migrationError: Exception) {
                    // Keep legacy products readable even if a rule/deployment blocks migration.
                    migrationError.printStackTrace()
                }
            }

            val remoteList = documentos
                .map { document -> produtoDoDocumento(document, artesao.id) }

            produtoDao.deleteByArtesao(artesao.id)
            produtoDao.insertAll(remoteList.map { it.toEntity() })
            remoteList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun buscarPorIdRemote(id: Long): Produto? {
        return try {
            val artesaoId = SessionManager.artesaoAtual.value?.id ?: return null
            val local = produtoDao.getById(id)
            val firestoreId = local?.firestoreId

            val document = if (!firestoreId.isNullOrBlank()) {
                FirebaseClient.produtosCollection.document(firestoreId).get().await()
            } else {
                FirebaseClient.produtosCollection
                    .whereEqualTo("id", id)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
            } ?: return null

            if (!document.exists()) return null
            produtoDoDocumento(document, artesaoId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun atualizarRemote(id: Long, dto: ProdutoUpdateDTO): Produto? {
        return try {
            val artesaoId = SessionManager.artesaoAtual.value?.id ?: return null
            val local = produtoDao.getById(id)
            val reference = if (!local?.firestoreId.isNullOrBlank()) {
                FirebaseClient.produtosCollection.document(local!!.firestoreId)
            } else {
                FirebaseClient.produtosCollection
                    .whereEqualTo("id", id)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.reference ?: return null
            }

            val updates = mutableMapOf<String, Any>()
            dto.nome?.let { updates["nome"] = it }
            dto.descricao?.let { updates["descricao"] = it }
            dto.preco?.let {
                updates["precoCentavos"] = kotlin.math.round(it * 100).toLong()
                updates["preco"] = FieldValue.delete()
            }
            updates["atualizadoEm"] = FieldValue.serverTimestamp()
            reference.update(updates).await()

            val existing = local ?: return buscarPorIdRemote(id)
            val updatedEntity = existing.copy(
                nome = dto.nome ?: existing.nome,
                descricao = dto.descricao ?: existing.descricao,
                preco = dto.preco ?: existing.preco
            )
            produtoDao.update(updatedEntity)
            updatedEntity.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Logical deletion; the Firestore document and QR history are preserved. */
    suspend fun deletarRemote(id: Long): Boolean {
        return try {
            val local = produtoDao.getById(id)
            val reference = if (!local?.firestoreId.isNullOrBlank()) {
                FirebaseClient.produtosCollection.document(local!!.firestoreId)
            } else {
                FirebaseClient.produtosCollection
                    .whereEqualTo("id", id)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.reference ?: return false
            }

            reference.update(
                mapOf(
                    "ativo" to false,
                    "atualizadoEm" to FieldValue.serverTimestamp()
                )
            ).await()
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
