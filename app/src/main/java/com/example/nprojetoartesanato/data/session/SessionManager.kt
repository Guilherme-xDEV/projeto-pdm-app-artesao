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

    fun iniciarSessao(artesao: Artesao) {

        _artesaoAtual.value = artesao
    }

    fun encerrarSessao() {

        _artesaoAtual.value = null
    }
}

/*

Now we have an explicit concept:

SessionManager
       ↓
artsaoCurrent

When Maria logs in:

SessionManager.startSessao(maria)

The application now knows:

Authenticated artesao:

Maria
id = 1
 */