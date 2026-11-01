package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.firebase.FirebaseClient
import com.example.nprojetoartesanato.data.local.dao.ArtesaoDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ArtesaoRepository(private val artesaoDao: ArtesaoDao) {

    val artesaoList: Flow<List<Artesao>> = artesaoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun signupRemote(dto: ArtesaoCreateDTO): Boolean {
        return try {
            val authResult = FirebaseClient.auth
                .createUserWithEmailAndPassword(dto.email, dto.senha)
                .await()

            val uid = authResult.user?.uid ?: return false
            val numericId = (uid.hashCode().toLong() and 0x7FFFFFFF)

            val artesaoData = hashMapOf(
                "id" to numericId,
                "uid" to uid,
                "nome" to dto.nome,
                "telefone" to dto.telefone,
                "email" to dto.email
            )

            FirebaseClient.artesaosCollection.document(uid).set(artesaoData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginRemote(email: String, senha: String): Boolean {
        return try {
            val authResult = FirebaseClient.auth
                .signInWithEmailAndPassword(email, senha)
                .await()

            val uid = authResult.user?.uid ?: return false

            val docSnapshot = FirebaseClient.artesaosCollection.document(uid).get().await()
            if (docSnapshot.exists()) {
                val numericId = docSnapshot.getLong("id") ?: (uid.hashCode().toLong() and 0x7FFFFFFF)
                val nome = docSnapshot.getString("nome") ?: ""
                val telefone = docSnapshot.getString("telefone") ?: ""

                val artesao = Artesao(
                    id = numericId,
                    nome = nome,
                    email = email,
                    telefone = telefone,
                    senha = ""
                )

                artesaoDao.insert(artesao.toEntity())
                SessionManager.iniciarSessao(artesao, uid)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun cadastrar(artesao: Artesao): Artesao {
        artesaoDao.insert(artesao.toEntity())
        return artesao
    }

    suspend fun autenticar(email: String, senha: String): Artesao? {
        val entity = artesaoDao.getByEmail(email)
        return if (entity != null && entity.senha == senha) entity.toDomain() else null
    }

    suspend fun buscarPorId(id: Long): Artesao? {
        return artesaoDao.getById(id)?.toDomain()
    }

    suspend fun atualizar(
        id: Long,
        dados: AtualizarArtesaoDTO
    ): Artesao? {
        val entity = artesaoDao.getById(id) ?: return null
        val updated = entity.copy(nome = dados.nome, telefone = dados.telefone)
        artesaoDao.update(updated)

        // Sync with Firestore if authenticated
        val uid = FirebaseClient.auth.currentUser?.uid
        if (uid != null) {
            val updates = mapOf(
                "nome" to dados.nome,
                "telefone" to dados.telefone
            )
            FirebaseClient.artesaosCollection.document(uid).update(updates)
        }

        return updated.toDomain()
    }

    suspend fun excluir(id: Long): Boolean {
        artesaoDao.deleteById(id)
        return true
    }
}
