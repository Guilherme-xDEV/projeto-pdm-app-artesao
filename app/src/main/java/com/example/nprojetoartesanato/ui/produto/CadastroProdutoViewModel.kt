package com.example.nprojetoartesanato.ui.produto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nprojetoartesanato.data.repository.ProdutoRepository
import com.example.nprojetoartesanato.data.session.SessionManager
import com.example.nprojetoartesanato.model.Produto

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

    var erro by mutableStateOf<String?>(null)
        private set

    var cadastroRealizado by mutableStateOf(false)
        private set

    fun atualizarNome(valor: String) {
        nome = valor
    }

    fun atualizarPreco(valor: String) {
        preco = valor
    }

    fun atualizarQuantidadeEstoque(valor: String) {
        quantidadeEstoque = valor
    }

    fun cadastrar(): Boolean {

        erro = null
        cadastroRealizado = false

        //1. Verify if there is a logged user

        var artesao = SessionManager.artesaoAtual.value

        if (artesao == null) {
            erro = "Nemhum artesão está autenticado."
            return false
        }

        //2. Validate fields

        if (nome.isBlank()) {
            erro = "Informe o nome do produto."
            return false
        }

        if (descricao.isBlank()) {
            erro = "Informe a descrição do produto."
            return false
        }

        if (preco.isBlank()) {
            erro = "Informe o preço do produto."
            return false
        }

        if (quantidadeEstoque.isBlank()) {
            erro = "Informe a quantidade em estoque."
            return false
        }

        //3. Convert price
        val precoConvertido = preco
            .replace(",", ".")
            .toDoubleOrNull()

        if (precoConvertido == null) {
            erro = "Informe um preço válido."
            return false
        }

        if (precoConvertido <= 0) {
            erro = "O preço deve ser maior que zero."
            return false
        }

        // 4. Convert stock
        val quantidadeConvertida =
            quantidadeEstoque.toIntOrNull()

        if (quantidadeConvertida == null) {
            erro = "Informe uma quantidade válida."
            return false
        }

        if (quantidadeConvertida < 0) {
            erro = "A quantidade em estoque não pode ser negativa."
            return false
        }

        //5. Create product
        val produto = Produto(
            id = 0,
            nome = nome.trim(),
            descricao = descricao.trim(),
            preco = precoConvertido,
            quantidadeEstoque = quantidadeConvertida,
            artesaoId = artesao.id
        )

        //6. Persist through repository
        produtoRepository.cadastrar(produto)
        cadastroRealizado = true
        limparformulario()
        return true
    }

    private fun limparformulario() {
        nome = ""
        descricao = ""
        preco = ""
        quantidadeEstoque = ""
    }
}