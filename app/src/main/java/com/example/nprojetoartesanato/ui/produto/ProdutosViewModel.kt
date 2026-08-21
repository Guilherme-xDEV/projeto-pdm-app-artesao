package com.example.nprojetoartesanato.ui.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProdutosViewModel : ViewModel() {

    private val produtoRepository = ProdutoRepository()

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos.asStateFlow()

    init {
        observarProdutos()
    }

    private fun observarProdutos() {
        viewModelScope.launch {
            SessionManager.artesaoAtual.collectLatest { artesao ->
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

    fun atualizarProduto(id: Long, nome: String, descricao: String, preco: Double): Boolean {

        if (nome.isBlank() || preco <= 0.0) {
            return false
        }

        val dto = AtualizarProdutoDTO(
            nome = nome.trim(),
            descricao = descricao.trim(),
            preco = preco
        )

        val produtoAtualizado = produtoRepository.atualizar(id, dto)
        return produtoAtualizado != null
    }
}
