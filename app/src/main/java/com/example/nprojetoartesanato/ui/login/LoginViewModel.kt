package com.example.nprojetoartesanato.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.data.session.SessionManager

class LoginViewModel : ViewModel() {

    //private val repository = ArtesaoRepository()

    var email by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    var erro by mutableStateOf<String?>(null)
        private set

//    fun atualizarEmail(valor: String) {
//        email = valor
//    }
//
//    fun atualizarSenha(valor: String) {
//        senha = valor
//    }

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onSenhaChange(newValue: String) {
        senha = newValue
    }

    fun login(): Boolean {

        if (email.isBlank() || senha.isBlank()) {

            erro = "Usuário e senha são obrigatórios."

            return false
        }

        val artesao = ArtesaoRepository.autenticar(
            email = email,
            senha = senha
        )

        if (artesao == null) {

            erro = "Usuário ou senha inválidos."

            return false
        }

        SessionManager.iniciarSessao(artesao)

        erro = null

        return true
    }
}

/*

Current situation:

val artesao = ArtesaoRepository.autenticar(...) searches in memory but later we can use
the same method to:

repository.autenticar()
       ↓
Retrofit
       ↓
POST /auth/login
       ↓
Spring Boot

 */