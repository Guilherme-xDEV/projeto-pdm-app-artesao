package com.example.nprojetoartesanato.data.session

import com.example.nprojetoartesanato.model.Artesao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _artesaoAtual =
        MutableStateFlow<Artesao?>(null)

    val artesaoAtual: StateFlow<Artesao?> =
        _artesaoAtual.asStateFlow()

    private var _token: String? = null
    val token: String? get() = _token

    fun iniciarSessao(artesao: Artesao, token: String? = null) {
        _artesaoAtual.value = artesao
        _token = token
    }

    fun encerrarSessao() {
        _artesaoAtual.value = null
        _token = null
    }
}