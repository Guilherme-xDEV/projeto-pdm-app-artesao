package com.example.nprojetoartesanato.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.nprojetoartesanato.model.Artesao
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    private const val PREF_NAME = "session_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ARTESAO = "logged_artesao"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private val _artesaoAtual = MutableStateFlow<Artesao?>(null)
    val artesaoAtual: StateFlow<Artesao?> = _artesaoAtual.asStateFlow()

    private var _token: String? = null
    val token: String? get() = _token

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        _token = prefs.getString(KEY_TOKEN, null)
        val artesaoJson = prefs.getString(KEY_ARTESAO, null)
        if (artesaoJson != null) {
            try {
                _artesaoAtual.value = gson.fromJson(artesaoJson, Artesao::class.java)
            } catch (e: Exception) {
                _artesaoAtual.value = null
            }
        }
    }

    fun iniciarSessao(artesao: Artesao, token: String? = null) {
        _artesaoAtual.value = artesao
        if (token != null) {
            _token = token
            prefs.edit { putString(KEY_TOKEN, token) }
        }
        val artesaoJson = gson.toJson(artesao)
        prefs.edit { putString(KEY_ARTESAO, artesaoJson) }
    }

    fun encerrarSessao() {
        _artesaoAtual.value = null
        _token = null
        prefs.edit { clear() }
    }
}
