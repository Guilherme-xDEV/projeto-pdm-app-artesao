package com.example.nprojetoartesanato.ui.produto

import androidx.lifecycle.ViewModel
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProdutoQrCodeViewModel(private val repository: ProdutoRepository) : ViewModel() {
    private val _produto = MutableStateFlow<Produto?>(null)
    val produto: StateFlow<Produto?> = _produto.asStateFlow()

    suspend fun carregarProduto(id: Long) {
        _produto.value = repository.buscarPorId(id)
    }
}
