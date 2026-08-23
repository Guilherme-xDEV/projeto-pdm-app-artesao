package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import kotlinx.coroutines.flow.StateFlow

object ArtesaoRepository {

    val artesaoList: StateFlow<List<Artesao>> = LocalDataStore.artesaoList
    fun cadastrar(artesao: Artesao): Artesao {

        return LocalDataStore.adicionarArtesao(artesao)
    }

    fun autenticar(
        usuario: String,
        senha: String
    ): Artesao? {

        return LocalDataStore.buscarArtesao(
            usuario = usuario,
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