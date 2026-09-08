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
import com.example.nprojetoartesanato.data.session.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VendaViewModel(
    private val vendaRepository: VendaRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val vendas: StateFlow<List<Venda>> =
        SessionManager.artesaoAtual.flatMapLatest { artesao ->
            if (artesao != null) {
                vendaRepository.observarTodas().map { list ->
                    list.filter { it.artesaoId == artesao.id }
                }
            } else {
                flow { emit(emptyList<Venda>()) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // INSIGHTS: Sales by Day (Last 7 Days)
    val salesInsights = vendas.map { lista ->
        getSalesByDay(lista)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getSalesByDay(emptyList()))

    private fun getSalesByDay(lista: List<Venda>): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        
        // Ensure we match even if format has dashes or slashes
        for (i in 6 downTo 0) {
            val calcCalendar = Calendar.getInstance()
            calcCalendar.add(Calendar.DAY_OF_YEAR, -i)
            
            val dateStrSlash = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calcCalendar.time)
            val dateStrDash = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calcCalendar.time)
            
            val count = lista.count { 
                val normalizedDate = it.dataHora.trim()
                normalizedDate.startsWith(dateStrSlash) || normalizedDate.startsWith(dateStrDash)
            }
            
            val dayName = when(calcCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Dom"
                Calendar.MONDAY -> "Seg"
                Calendar.TUESDAY -> "Ter"
                Calendar.WEDNESDAY -> "Qua"
                Calendar.THURSDAY -> "Qui"
                Calendar.FRIDAY -> "Sex"
                Calendar.SATURDAY -> "Sáb"
                else -> ""
            }
            result.add(dayName to count)
        }
        return result
    }

    // INSIGHTS: Top 3 Products
    val topProducts = vendas.map { lista ->
        lista.groupBy { it.produto }
            .mapValues { entry -> entry.value.sumOf { it.quantidade } }
            .toList()
            .sortedByDescending { it.second }
            .take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val produtosDisponiveis: StateFlow<List<Produto>> =
        SessionManager.artesaoAtual.flatMapLatest { artesao ->
            if (artesao != null) {
                produtoRepository.observarPorArtesao(artesao.id)
            } else {
                flow { emit(emptyList<Produto>()) }
            }
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

    suspend fun buscarPorQrCode(conteudo: String): Produto? {
        return produtoRepository.listarTodos().find { it.qrCodeId == conteudo }
    }
}
