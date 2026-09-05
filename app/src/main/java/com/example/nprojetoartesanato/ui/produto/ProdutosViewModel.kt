package com.example.nprojetoartesanato.ui.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.network.dto.ProdutoUpdateDTO
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProdutosViewModel(private val produtoRepository: ProdutoRepository) : ViewModel() {

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observarProdutos()
        carregarProdutos()
    }

    private fun observarProdutos() {
        viewModelScope.launch {
            SessionManager.artesaoAtual.collect { artesao ->
                if (artesao != null) {
                    produtoRepository
                        .observarPorArtesao(artesao.id)
                        .collect { produtos ->
                            _produtos.value = produtos
                        }
                } else {
                    _produtos.value = emptyList()
                }
            }
        }
    }

    fun carregarProdutos() {
        viewModelScope.launch {
            _isLoading.value = true
            produtoRepository.listarMeusProdutosRemote()
            _isLoading.value = false
        }
    }

    fun atualizarProduto(id: Long, nome: String, descricao: String, preco: Double) {
        viewModelScope.launch {
            val dto = ProdutoUpdateDTO(
                nome = nome.trim(),
                descricao = descricao.trim(),
                preco = preco
            )
            val produtoAtualizado = produtoRepository.atualizarRemote(id, dto)
            if (produtoAtualizado != null) {
                carregarProdutos()
            }
        }
    }

    fun deletarProduto(id: Long) {
        viewModelScope.launch {
            val sucesso = produtoRepository.deletarRemote(id)
            if (sucesso) {
                carregarProdutos()
            }
        }
    }
}
