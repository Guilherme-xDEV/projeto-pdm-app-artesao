package com.example.nprojetoartesanato.ui.produto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nprojetoartesanato.data.network.dto.ProdutoCreateDTO
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.launch
import java.util.UUID

class CadastroProdutoViewModel : ViewModel() {

    private val produtoRepository = ProdutoRepository()

    var nome by mutableStateOf("")
        private set

    var descricao by mutableStateOf("")
        private set

    var preco by mutableStateOf("")
        private set

    var quantidadeEstoque by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    var cadastroRealizado by mutableStateOf(false)
        private set

    var ultimoProdutoCadastrado by mutableStateOf<Pair<String, String>?>(null)
        private set

    fun atualizarNome(valor: String) {
        nome = valor
    }

    fun atualizarDescricao(valor: String) {
        descricao = valor
    }

    fun atualizarPreco(valor: String) {
        preco = valor
    }

    fun atualizarQuantidadeEstoque(valor: String) {
        quantidadeEstoque = valor
    }

    fun cadastrar() {

        erro = null
        cadastroRealizado = false

        //1. Verify if there is a logged user
        val artesao = SessionManager.artesaoAtual.value

        if (artesao == null) {
            erro = "Nenhum artesão está autenticado."
            return
        }

        //2. Validate fields

        if (nome.isBlank() || descricao.isBlank() || preco.isBlank() || quantidadeEstoque.isBlank()) {
            erro = "Todos os campos são obrigatórios."
            return
        }

        //3. Convert price
        val precoDouble = preco
            .replace(",", ".")
            .toDoubleOrNull()

        if (precoDouble == null || precoDouble <= 0) {
            erro = "Informe um preço válido."
            return
        }

        // 4. Convert stock
        val quantidadeInt =
            quantidadeEstoque.toIntOrNull()

        if (quantidadeInt == null || quantidadeInt < 0) {
            erro = "Informe uma quantidade válida."
            return
        }

        isLoading = true

        viewModelScope.launch {
            val qrCodeId = UUID.randomUUID().toString()
            val dto = ProdutoCreateDTO(
                nome = nome.trim(),
                descricao = descricao.trim(),
                preco = precoDouble,
                quantidadeEstoque = quantidadeInt,
                qrCodeId = qrCodeId
            )

            val produto = produtoRepository.cadastrarRemote(dto)
            
            isLoading = false
            if (produto != null) {
                ultimoProdutoCadastrado = Pair(produto.nome, produto.qrCodeId)
                cadastroRealizado = true
                limparformulario()
            } else {
                erro = "Erro ao cadastrar produto no servidor."
            }
        }
    }

    private fun limparformulario() {
        nome = ""
        descricao = ""
        preco = ""
        quantidadeEstoque = ""
    }
}