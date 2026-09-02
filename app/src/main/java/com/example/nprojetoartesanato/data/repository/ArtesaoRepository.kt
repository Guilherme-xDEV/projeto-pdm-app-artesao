package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import com.example.nprojetoartesanato.data.network.RetrofitClient
import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.LoginRequest
import com.example.nprojetoartesanato.data.session.SessionManager
import kotlinx.coroutines.flow.StateFlow

object ArtesaoRepository {

    private val authService = RetrofitClient.authApiService

    val artesaoList: StateFlow<List<Artesao>> = LocalDataStore.artesaoList

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
                        telefone = "", // These could be fetched later or returned by login
                        identificacao = "",
                        senha = ""
                    )
                    SessionManager.iniciarSessao(artesao, authResponse.token)
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }
    fun cadastrar(artesao: Artesao): Artesao {

        return LocalDataStore.adicionarArtesao(artesao)
    }

    fun autenticar(
        email: String,
        senha: String
    ): Artesao? {

        return LocalDataStore.buscarArtesao(
            email = email,
            senha = senha
        )
    }

    fun buscarPorId(id: Long): Artesao? {

        return LocalDataStore.buscarArtesaoPorId(id)
    }

    fun listarTodos(): List<Artesao> {

        return LocalDataStore.listarArtesaos()
    }

    fun atualizar(
        id: Long,
        dados: AtualizarArtesaoDTO
    ): Artesao? {

        return LocalDataStore.atualizarArtesao(id, dados)
    }

    fun excluir(id: Long): Boolean {

        return LocalDataStore.excluirArtesao(id)
    }
}