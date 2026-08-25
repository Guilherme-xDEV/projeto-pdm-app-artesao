package com.example.nprojetoartesanato.ui.venda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.VendaRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class VendaViewModel : ViewModel() {

    private val vendaRepository = VendaRepository()

    val vendas: StateFlow<List<Venda>> =
        vendaRepository.observarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Registra a venda de [produto] em nome do artesão logado (dá baixa
     * de estoque e adiciona ao histórico). Retorna true se a venda foi
     * registrada com sucesso.
     */
    fun registrarVenda(produto: Produto, quantidade: Int = 1): Boolean {
        val vendedorNome = SessionManager.artesaoAtual.value?.nome ?: "Desconhecido"
        return vendaRepository.registrarVenda(produto, vendedorNome, quantidade) != null
    }
}