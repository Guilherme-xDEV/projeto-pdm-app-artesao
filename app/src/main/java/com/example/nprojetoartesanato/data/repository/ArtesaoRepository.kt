package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.dao.ArtesaoDao
import com.example.nprojetoartesanato.data.local.entities.toDomain
import com.example.nprojetoartesanato.data.local.entities.toEntity
import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.LoginRequest
import com.example.nprojetoartesanato.data.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtesaoRepository(private val artesaoDao: ArtesaoDao) {

    private val authService = RetrofitClient.authApiService

    val artesaoList: Flow<List<Artesao>> = artesaoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun signupRemote(dto: ArtesaoCreateDTO): Boolean {
        return try {
            val response = authService.signup(dto)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginRemote(email: String, senha: String): Boolean {
        return try {
            val response = authService.login(LoginRequest(email, senha))
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    val artesao = Artesao(
                        id = authResponse.artesaoId,
                        nome = authResponse.nome,
                        email = email,
                        telefone = "",
                        senha = ""
                    )
                    // Persist locally
                    artesaoDao.insert(artesao.toEntity())
                    SessionManager.iniciarSessao(artesao, authResponse.token)
                    return true
                }
            }
            false
        } catch (e: Exception) {
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
        return updated.toDomain()
    }

    suspend fun excluir(id: Long): Boolean {
        artesaoDao.deleteById(id)
        return true
    }
}
