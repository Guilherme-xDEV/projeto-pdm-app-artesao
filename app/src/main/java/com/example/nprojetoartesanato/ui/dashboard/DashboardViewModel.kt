package com.example.nprojetoartesanato.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto

class DashboardViewModel : ViewModel() {

    private val produtoRepository = ProdutoRepository()

    val artesaoAtual =
        SessionManager.artesaoAtual

    var produtos by mutableStateOf<List<Produto>>(
        emptyList()
    )
        private set

    fun carregarProdutos() {

        val artesao =
            artesaoAtual.value
                ?: return

        produtos =
            produtoRepository.buscarPorArtesao(
                artesao.id
            )
    }
}