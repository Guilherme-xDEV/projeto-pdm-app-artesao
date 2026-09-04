package com.example.nprojetoartesanato.ui.venda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.repository.VendaRepository
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VendaViewModel : ViewModel() {

    private val vendaRepository = VendaRepository()
    private val produtoRepository = ProdutoRepository()

    val vendas: StateFlow<List<Venda>> =
        vendaRepository.observarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val produtosDisponiveis: StateFlow<List<Produto>> =
        flow {
            emit(produtoRepository.listarTodos())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshVendas()
    }

    fun refreshVendas() {
        viewModelScope.launch {
            vendaRepository.sincronizarHistoricoRemote()
        }
    }

    /**
     * Registra a venda de [produto] remotamente (dá baixa
     * de estoque e adiciona ao histórico no banco). Retorna true se a venda foi
     * registrada com sucesso.
     */
    suspend fun registrarVenda(produto: Produto, quantidade: Int = 1): Boolean {
        return vendaRepository.registrarVendaRemote(produto.id, quantidade) != null
    }
}
