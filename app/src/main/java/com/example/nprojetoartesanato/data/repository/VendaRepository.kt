package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.firebase.FirebaseClient
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.dao.VendaDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong

class VendaRepository(
    private val vendaDao: VendaDao,
    private val produtoDao: ProdutoDao
) {

    private fun idLocal(documentId: String): Long =
        (documentId.hashCode().toLong() and 0x7FFFFFFF)

    private fun formatter() = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    private fun dataHoraDoDocumento(document: com.google.firebase.firestore.DocumentSnapshot): String {
        val timestamp = document.getTimestamp("dataHora")
        if (timestamp != null) return formatter().format(timestamp.toDate())
        return document.getString("dataHora") ?: ""
    }

    private fun totalCentavosDoDocumento(document: com.google.firebase.firestore.DocumentSnapshot): Long {
        val canonical = document.get("totalCentavos") as? Number
        if (canonical != null) return canonical.toLong()
        val legacy = document.get("valorTotal") as? Number
        return ((legacy?.toDouble() ?: 0.0) * 100.0).roundToLong()
    }

    /**
     * Registers a canonical sale and decrements stock in the same Firestore
     * transaction. Product references always use the Firestore document ID.
     */
    suspend fun registrarVendaRemote(produtoId: Long, quantidade: Int): Venda? {
        return try {
            if (quantidade < 1) return null

            val artesaoAtual = SessionManager.artesaoAtual.value ?: return null
            val uid = FirebaseClient.auth.currentUser?.uid ?: return null
            val produtoEntity = produtoDao.getById(produtoId) ?: return null

            val produtoReference: DocumentReference = if (produtoEntity.firestoreId.isNotBlank()) {
                FirebaseClient.produtosCollection.document(produtoEntity.firestoreId)
            } else {
                FirebaseClient.produtosCollection
                    .whereEqualTo("id", produtoId)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.reference ?: return null
            }

            val vendaDocRef = FirebaseClient.vendasCollection.document()
            val numericVendaId = idLocal(vendaDocRef.id)
            var createdVenda: Venda? = null

            FirebaseClient.db.runTransaction { transaction ->
                val snapshot = transaction.get(produtoReference)
                if (!snapshot.exists()) {
                    throw IllegalStateException("Produto não encontrado no Firestore.")
                }

                val estoqueAtual = (snapshot.get("quantidadeEstoque") as? Number)?.toInt() ?: 0
                val precoCentavos = (snapshot.get("precoCentavos") as? Number)?.toLong()
                    ?: ((snapshot.get("preco") as? Number)?.toDouble()?.times(100.0)?.roundToLong()
                        ?: 0L)
                if (estoqueAtual < 0 || quantidade > estoqueAtual) {
                    throw IllegalStateException("Estoque insuficiente para registrar a venda.")
                }
                if (precoCentavos <= 0) {
                    throw IllegalStateException("O preço deste produto é inválido.")
                }

                val totalCentavos = precoCentavos * quantidade
                val produtoNome = snapshot.getString("nome") ?: produtoEntity.nome
                val artesaoIdRemoto = snapshot.get("artesaoId") ?: uid
                val artesaoNome = snapshot.getString("artesaoNome") ?: artesaoAtual.nome
                val updates = mutableMapOf<String, Any>(
                    "quantidadeEstoque" to (estoqueAtual - quantidade),
                    "ultimaVendaId" to vendaDocRef.id,
                    "atualizadoEm" to FieldValue.serverTimestamp()
                )

                // Upgrade old price fields during the owner's next stock operation.
                if (snapshot.get("precoCentavos") == null) {
                    updates["precoCentavos"] = precoCentavos
                    updates["preco"] = FieldValue.delete()
                    updates["artesaoNome"] = artesaoNome
                    updates["firestoreId"] = produtoReference.id
                }

                transaction.update(produtoReference, updates)
                transaction.set(
                    vendaDocRef,
                    mapOf(
                        "produtoId" to produtoReference.id,
                        "produtoNome" to produtoNome,
                        "artesaoId" to artesaoIdRemoto,
                        "artesaoNome" to artesaoNome,
                        "vendedorId" to uid,
                        "vendedorNome" to artesaoAtual.nome,
                        "precoUnitarioCentavos" to precoCentavos,
                        "quantidade" to quantidade,
                        "totalCentavos" to totalCentavos,
                        "dataHora" to FieldValue.serverTimestamp()
                    )
                )

                createdVenda = Venda(
                    id = numericVendaId,
                    produto = produtoNome,
                    artesao = artesaoNome,
                    artesaoId = artesaoAtual.id,
                    vendedor = artesaoAtual.nome,
                    valor = "R$ %.2f".format(totalCentavos / 100.0),
                    quantidade = quantidade,
                    dataHora = formatter().format(Date())
                )
                null
            }.await()

            val updatedProduct = produtoEntity.copy(
                quantidadeEstoque = (produtoEntity.quantidadeEstoque - quantidade).coerceAtLeast(0),
                firestoreId = produtoReference.id
            )
            produtoDao.update(updatedProduct)

            createdVenda?.let { venda ->
                vendaDao.insert(venda.toEntity())
            }
            createdVenda
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Synchronizes both canonical UID-owned and historical numeric-ID sales. */
    suspend fun sincronizarHistoricoRemote(): List<Venda> {
        return try {
            val artesaoAtual = SessionManager.artesaoAtual.value ?: return emptyList()
            val uid = FirebaseClient.auth.currentUser?.uid ?: return emptyList()
            val vendasCollection = FirebaseClient.vendasCollection

            val canonicas = vendasCollection
                .whereEqualTo("artesaoId", uid)
                .get()
                .await()
            val legadas = vendasCollection
                .whereEqualTo("artesaoId", artesaoAtual.id)
                .get()
                .await()

            val documentos = (canonicas.documents + legadas.documents).distinctBy { it.id }
            val remoteList = documentos.map { document ->
                val quantidade = (document.get("quantidade") as? Number)?.toInt() ?: 1
                val totalCentavos = totalCentavosDoDocumento(document)
                val valorTotal = totalCentavos / 100.0
                val produtoNome = document.getString("produtoNome") ?: ""
                val artesaoNome = document.getString("artesaoNome") ?: artesaoAtual.nome
                val vendedorNome = document.getString("vendedorNome") ?: artesaoNome

                Venda(
                    id = document.getLong("id") ?: idLocal(document.id),
                    produto = produtoNome,
                    artesao = artesaoNome,
                    artesaoId = artesaoAtual.id,
                    vendedor = vendedorNome,
                    valor = "R$ %.2f".format(valorTotal),
                    quantidade = quantidade,
                    dataHora = dataHoraDoDocumento(document)
                )
            }

            vendaDao.deleteAll()
            vendaDao.insertAll(remoteList.map { it.toEntity() })
            remoteList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun registrarVenda(
        produto: Produto,
        vendedorNome: String,
        quantidade: Int = 1
    ): Venda? {
        val entity = produtoDao.getById(produto.id) ?: return null
        val updated = entity.copy(
            quantidadeEstoque = (entity.quantidadeEstoque - quantidade).coerceAtLeast(0)
        )
        produtoDao.update(updated)

        val venda = Venda(
            id = 0,
            produto = updated.nome,
            artesao = "Local",
            artesaoId = updated.artesaoId,
            vendedor = vendedorNome,
            valor = "R$ %.2f".format(updated.preco * quantidade),
            quantidade = quantidade,
            dataHora = formatter().format(Date())
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
