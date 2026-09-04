package com.example.nprojetoartesanato.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ArtesaoRepository) : ViewModel() {

    private val _artesao = MutableStateFlow<Artesao?>(null)
    val artesao: StateFlow<Artesao?> = _artesao.asStateFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            SessionManager.artesaoAtual.collect {
                _artesao.value = it
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        SessionManager.encerrarSessao()
        onSuccess()
    }

    fun atualizarPerfil(nome: String, telefone: String) {
        val currentArtesao = _artesao.value ?: return
        
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val dto = AtualizarArtesaoDTO(nome = nome, telefone = telefone)
            val atualizado = repository.atualizar(currentArtesao.id, dto)
            
            if (atualizado != null) {
                SessionManager.iniciarSessao(atualizado)
                _uiState.value = ProfileUiState.Success("Perfil atualizado com sucesso!")
            } else {
                _uiState.value = ProfileUiState.Error("Erro ao atualizar perfil.")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = ProfileUiState.Idle
    }
}

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val message: String) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
