package com.example.nprojetoartesanato.ui.cadastroArtesao

import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.network.dto.ArtesaoCreateDTO
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.model.Artesao
import kotlinx.coroutines.launch

class CadastroArtesaoViewModel(private val repository: ArtesaoRepository) : ViewModel() {

    var nome by mutableStateOf("")
        private set

    var telefone by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    // Validation States
    var tentouSubmeter by mutableStateOf(false)
        private set

    val nomeErro by derivedStateOf {
        if (tentouSubmeter && nome.trim().length < 3) "Nome deve ter pelo menos 3 letras" else null
    }

    val telefoneErro by derivedStateOf {
        if (tentouSubmeter && (telefone.trim().length < 10 || telefone.trim().length > 11)) 
            "Telefone inválido (DDD + número)" else null
    }

    val emailErro by derivedStateOf {
        if (tentouSubmeter && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) 
            "E-mail inválido" else null
    }

    val senhaErro by derivedStateOf {
        if (tentouSubmeter && senha.length < 6) "A senha deve ter no mínimo 6 caracteres" else null
    }

    fun atualizarNome(valor: String) {
        nome = valor
    }

    fun atualizarTelefone(valor: String) {
        // Only numbers for simplicity
        telefone = valor.filter { it.isDigit() }
    }

    fun atualizarEmail(valor: String) {
        email = valor
    }

    fun atualizarSenha(valor: String) {
        senha = valor
    }

    fun cadastrar(onSuccess: () -> Unit) {
        tentouSubmeter = true
        
        if (nomeErro != null || telefoneErro != null || emailErro != null || senhaErro != null) {
            erro = "Por favor, corrija os erros no formulário."
            return
        }

        isLoading = true
        erro = null

        viewModelScope.launch {
            val dto = ArtesaoCreateDTO(nome.trim(), telefone.trim(), email.trim(), senha)
            
            // Try remote signup
            val remoteSuccess = repository.signupRemote(dto)
            
            if (remoteSuccess) {
                isLoading = false
                onSuccess()
            } else {
                // For now, even if remote fails, we add locally for testing
                val artesao = Artesao(0, nome.trim(), telefone.trim(), email.trim(), senha)
                repository.cadastrar(artesao)
                isLoading = false
                onSuccess()
            }
        }
    }
}
