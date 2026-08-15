package com.example.nprojetoartesanato.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val produtoRepository = ProdutoRepository()

    val artesaoAtual =
        SessionManager.artesaoAtual

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())

    val produtos: StateFlow<List<Produto>> =
        _produtos.asStateFlow()

    init {
        observarProdutos()
    }

//    fun carregarProdutos() {
//
//        val artesao =
//            artesaoAtual.value
//                ?: return
//
//        produtos =
//            produtoRepository.buscarPorArtesao(
//                artesao.id
//            )
//    }

    private fun observarProdutos() {

        viewModelScope.launch {
            artesaoAtual.collectLatest { artesao ->

                if (artesao == null) {

                    _produtos.value = emptyList()

                    return@collectLatest
                }

                produtoRepository
                    .observarPorArtesao(artesao.id)
                    .collect { produtos ->
                        _produtos.value = produtos
                    }
            }
        }
    }
}