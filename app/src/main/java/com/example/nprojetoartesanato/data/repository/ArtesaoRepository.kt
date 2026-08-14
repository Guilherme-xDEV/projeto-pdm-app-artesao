package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Artesao

class ArtesaoRepository {

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
}

/*

Current:
ArtesaoRepository
      ↓
LocalDataStore

Later:
ArtesaoRepository
      ↓
Retrofit
      ↓
Spring Boot
 */