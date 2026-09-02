package com.example.nprojetoartesanato.ui.cadastroArtesao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.model.Artesao
import kotlinx.coroutines.launch

class CadastroArtesaoViewModel : ViewModel() {

    var nome by mutableStateOf("")
        private set

    var telefone by mutableStateOf("")
        private set

    var identificacao by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
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

    fun atualizarEmail(valor: String) {
        email = valor
    }

    fun atualizarSenha(valor: String) {
        senha = valor
    }

    fun cadastrar(onSuccess: () -> Unit) {
        if (
            nome.isBlank() ||
            telefone.isBlank() ||
            identificacao.isBlank() ||
            email.isBlank() ||
            senha.isBlank()
        ) {
            erro = "Todos os campos são obrigatórios."
            return
        }

        isLoading = true
        erro = null

        viewModelScope.launch {
            val dto = ArtesaoCreateDTO(nome, telefone, identificacao, email, senha)
            
            // Try remote signup
            val remoteSuccess = ArtesaoRepository.signupRemote(dto)
            
            if (remoteSuccess) {
                isLoading = false
                onSuccess()
            } else {
                // For now, even if remote fails, we add locally for testing
                val artesao = Artesao(0, nome, telefone, identificacao, email, senha)
                ArtesaoRepository.cadastrar(artesao)
                isLoading = false
                onSuccess()
            }
        }
    }
}