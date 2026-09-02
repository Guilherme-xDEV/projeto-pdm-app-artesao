package com.example.nprojetoartesanato.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onSenhaChange(newValue: String) {
        senha = newValue
    }

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || senha.isBlank()) {
            erro = "E-mail e senha são obrigatórios."
            return
        }

        isLoading = true
        erro = null

        viewModelScope.launch {
            // First, try remote login
            val remoteSuccess = ArtesaoRepository.loginRemote(email, senha)
            
            if (remoteSuccess) {
                isLoading = false
                onSuccess()
            } else {
                // Fallback to local login for development/offline
                val artesao = ArtesaoRepository.autenticar(email, senha)
                isLoading = false
                
                if (artesao != null) {
                    SessionManager.iniciarSessao(artesao)
                    onSuccess()
                } else {
                    erro = "E-mail ou senha inválidos."
                }
            }
        }
    }
}