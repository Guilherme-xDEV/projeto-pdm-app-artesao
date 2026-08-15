package com.example.nprojetoartesanato.ui.cadastroArtesao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.model.Artesao

class CadastroArtesaoViewModel : ViewModel() {

    var nome by mutableStateOf("")
        private set

    var telefone by mutableStateOf("")
        private set

    var identificacao by mutableStateOf("")
        private set

    var usuario by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    fun atualizarNome(valor: String) {
        nome = valor
    }

    fun atualizarTelefone(valor: String) {
        telefone = valor
    }

    fun atualizarIdentificacao(valor: String) {
        identificacao = valor
    }

    fun atualizarUsuario(valor: String) {
        usuario = valor
    }

    fun atualizarSenha(valor: String) {
        senha = valor
    }

    fun cadastrar(): Boolean {

        if (
            nome.isBlank() ||
            telefone.isBlank() ||
            identificacao.isBlank() ||
            usuario.isBlank() ||
            senha.isBlank()
        ) {
            erro = "Todos os campos são obrigatórios."
            return false
        }

        val artesao = Artesao(
            id = 0,
            nome = nome,
            telefone = telefone,
            identificacao = identificacao,
            usuario = usuario,
            senha = senha
        )

        ArtesaoRepository.cadastrar(artesao)

        erro = null

        return true
    }
}

/*
Since this project has no Repository yet, this ViewModel class will be used to
control the state of the form submitted by the created 'Artesao' when he enters
data at the 'CadastroArtesaoScreen'. Think of this as a non-persistent database.

CadastroArtesaoScreen
        ↓
ViewModel
        ↓
repository.cadastrar()
        ↓
LocalDataStore
        ↓
artesão armazenado
 */