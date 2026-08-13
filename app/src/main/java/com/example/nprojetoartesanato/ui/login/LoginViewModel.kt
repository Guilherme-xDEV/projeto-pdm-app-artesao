package com.example.nprojetoartesanato.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var usuario by mutableStateOf("")
        private set

    var senha by mutableStateOf("")
        private set

    fun onUsuarioChange(newValue: String) {
        usuario = newValue
    }

    fun onSenhaChange(newValue: String) {
        senha = newValue
    }
}
