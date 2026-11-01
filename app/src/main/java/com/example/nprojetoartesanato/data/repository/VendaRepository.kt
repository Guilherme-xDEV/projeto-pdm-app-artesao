package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.firebase.FirebaseClient
import com.example.nprojetoartesanato.data.local.dao.ProdutoDao
import com.example.nprojetoartesanato.data.local.dao.VendaDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendaRepository(
    private val vendaDao: VendaDao,
    private val produtoDao: ProdutoDao
) {

    suspend fun registrarVendaRemote(produtoId: Long, quantidade: Int): Venda? {
        return try {
            val artesaoAtual = SessionManager.artesaoAtual.value
            val artesaoId = artesaoAtual?.id ?: 0L
            val artesaoNome = artesaoAtual?.nome ?: "Artesão"

            val querySnapshot = FirebaseClient.produtosCollection
                .whereEqualTo("id", produtoId)
                .get()
                .await()

            val produtoDoc = querySnapshot.documents.firstOrNull() ?: return null

            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            val dataHoraStr = formato.format(Date())

            var createdVenda: Venda? = null

            FirebaseClient.db.runTransaction { transaction ->
                val snapshot = transaction.get(produtoDoc.reference)
                val estoqueAtual = snapshot.getLong("quantidadeEstoque")?.toInt() ?: 0
                val preco = snapshot.getDouble("preco") ?: 0.0
                val produtoNome = snapshot.getString("nome") ?: "Produto"

                val novoEstoque = (estoqueAtual - quantidade).coerceAtLeast(0)
                transaction.update(produtoDoc.reference, "quantidadeEstoque", novoEstoque)

                val vendaDocRef = FirebaseClient.vendasCollection.document()
                val numericVendaId = (vendaDocRef.id.hashCode().toLong() and 0x7FFFFFFF)
                val valorTotal = preco * quantidade

                val vendaData = hashMapOf(
                    "id" to numericVendaId,
                    "produtoNome" to produtoNome,
                    "artesaoNome" to artesaoNome,
                    "artesaoId" to artesaoId,
                    "vendedorNome" to artesaoNome,
                    "valorTotal" to valorTotal,
                    "quantidade" to quantidade,
                    "dataHora" to dataHoraStr
                )

                transaction.set(vendaDocRef, vendaData)

                createdVenda = Venda(
                    id = numericVendaId,
                    produto = produtoNome,
                    artesao = artesaoNome,
                    artesaoId = artesaoId,
                    vendedor = artesaoNome,
                    valor = "R$ %.2f".format(valorTotal),
                    quantidade = quantidade,
                    dataHora = dataHoraStr
                )
            }.await()

            createdVenda?.let { venda ->
                val produtoEntity = produtoDao.getById(produtoId)
                if (produtoEntity != null) {
                    val updated = produtoEntity.copy(
                        quantidadeEstoque = (produtoEntity.quantidadeEstoque - quantidade).coerceAtLeast(0)
                    )
                    produtoDao.update(updated)
                }
                vendaDao.insert(venda.toEntity())
            }

            createdVenda
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sincronizarHistoricoRemote(): List<Venda> {
        return try {
            val artesaoId = SessionManager.artesaoAtual.value?.id ?: 0L
            val querySnapshot = FirebaseClient.vendasCollection
                .whereEqualTo("artesaoId", artesaoId)
                .get()
                .await()

            val remoteList = querySnapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id") ?: return@mapNotNull null
                val produtoNome = doc.getString("produtoNome") ?: ""
                val artesaoNome = doc.getString("artesaoNome") ?: ""
                val vendedorNome = doc.getString("vendedorNome") ?: ""
                val valorTotal = doc.getDouble("valorTotal") ?: 0.0
                val quantidade = doc.getLong("quantidade")?.toInt() ?: 1
                val dataHora = doc.getString("dataHora") ?: ""

                Venda(
                    id = id,
                    produto = produtoNome,
                    artesao = artesaoNome,
                    artesaoId = artesaoId,
                    vendedor = vendedorNome,
                    valor = "R$ %.2f".format(valorTotal),
                    quantidade = quantidade,
                    dataHora = dataHora
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

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        val venda = Venda(
            id = 0,
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
