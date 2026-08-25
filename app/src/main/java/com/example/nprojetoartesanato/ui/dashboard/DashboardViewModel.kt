package com.example.nprojetoartesanato.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.repository.VendaRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val produtoRepository = ProdutoRepository()
    private val vendaRepository = VendaRepository()

    val artesaoAtual =
        SessionManager.artesaoAtual

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos.asStateFlow()

    private val _vendas = MutableStateFlow<List<Venda>>(emptyList())
    val vendas: StateFlow<List<Venda>> = _vendas.asStateFlow()

    init {
        observarDados()
    }

    private fun observarDados() {
        viewModelScope.launch {
            artesaoAtual.collectLatest { artesao ->
                if (artesao == null) {
                    _produtos.value = emptyList()
                    _vendas.value = emptyList()
                    return@collectLatest
                }

                // Observe products
                launch {
                    produtoRepository
                        .observarPorArtesao(artesao.id)
                        .collect { produtos ->
                            _produtos.value = produtos
                        }
                }

                // Observe sales
                launch {
                    vendaRepository
                        .observarTodas()
                        .collect { todasAsVendas ->
                            _vendas.value = todasAsVendas.filter { it.artesaoId == artesao.id }
                        }
                }
            }
        }
    }
}
