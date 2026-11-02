package com.example.nprojetoartesanato.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.firebase.FirebaseClient
import com.example.nprojetoartesanato.data.repository.ArtesaoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Artesao
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(private val repository: ArtesaoRepository) : ViewModel() {

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
            val remoteSuccess = repository.loginRemote(email, senha)
            
            if (remoteSuccess) {
                isLoading = false
                onSuccess()
            } else {
                // Fallback to local login for development/offline
                val artesao = repository.autenticar(email, senha)
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

    fun loginComGoogle(idToken: String, googleName: String, googleEmail: String, onSuccess: () -> Unit) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseClient.auth
                    .signInWithCredential(credential)
                    .await()

                val uid = authResult.user?.uid
                if (uid != null) {
                    var numericId = (uid.hashCode().toLong() and 0x7FFFFFFF)
                    var nome = googleName.ifBlank { "Artesão" }
                    var email = googleEmail

                    // Try syncing with Firestore, but don't block login if Firestore security rules reject access
                    try {
                        val docRef = FirebaseClient.artesaosCollection.document(uid)
                        val snapshot = docRef.get().await()

                        if (snapshot.exists()) {
                            numericId = snapshot.getLong("id") ?: numericId
                            nome = snapshot.getString("nome") ?: nome
                            email = snapshot.getString("email") ?: email
                        } else {
                            val artesaoData = hashMapOf(
                                "id" to numericId,
                                "uid" to uid,
                                "nome" to nome,
                                "telefone" to "",
                                "email" to email
                            )
                            docRef.set(artesaoData).await()
                        }
                    } catch (firestoreError: Exception) {
                        firestoreError.printStackTrace()
                    }

                    val artesao = Artesao(
                        id = numericId,
                        nome = nome,
                        email = email,
                        telefone = "",
                        senha = ""
                    )

                    // Ensure local Room DB persists this Artesao record
                    repository.cadastrar(artesao)

                    SessionManager.iniciarSessao(artesao, uid)
                    isLoading = false
                    onSuccess()
                } else {
                    isLoading = false
                    erro = "Falha na autenticação com o Google."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
                erro = e.localizedMessage ?: "Erro ao autenticar com o Google."
            }
        }
    }
}
