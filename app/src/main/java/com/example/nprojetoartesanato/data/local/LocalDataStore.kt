package com.example.nprojetoartesanato.data.local

import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.Venda
import com.example.nprojetoartesanato.model.dto.AtualizarArtesaoDTO
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalDataStore {

    private val _artesaoList = MutableStateFlow<List<Artesao>>(emptyList())
    val artesaoList: StateFlow<List<Artesao>> = _artesaoList.asStateFlow()

    private val _produtoList = MutableStateFlow<List<Produto>>(emptyList())
    val produtoList: StateFlow<List<Produto>> = _produtoList.asStateFlow()

    private val _vendaList = MutableStateFlow<List<Venda>>(emptyList())
    val vendaList: StateFlow<List<Venda>> = _vendaList.asStateFlow()

    private var nextArtesaoId = 1L
    private var nextProdutoId = 1L
    private var nextVendaId = 1L

    // Artisan CRUD Methods
    fun adicionarArtesao(
        artesao: Artesao
    ): Artesao {

        val novoArtesao = artesao.copy(
            id = nextArtesaoId++
        )

        _artesaoList.value += novoArtesao
        return novoArtesao
    }

    fun buscarArtesao(
        email: String,
        senha: String
    ): Artesao? {

        return _artesaoList.value.find {
            it.email == email &&
                    it.senha == senha
        }
    }

    fun buscarArtesaoPorId(
        id: Long
    ): Artesao? {

        return _artesaoList.value.find { it.id == id }
    }

    fun listarArtesaos(): List<Artesao> {
        return _artesaoList.value
    }

    fun atualizarArtesao(
        id: Long,
        dados: AtualizarArtesaoDTO
    ): Artesao? {
        val artesaoAtual = buscarArtesaoPorId(id) ?: return null

        val artesaoAtualizado = artesaoAtual.copy(
            nome = dados.nome,
            telefone = dados.telefone
        )

        _artesaoList.value = _artesaoList.value.map { artesao ->
            if (artesao.id == id) artesaoAtualizado else artesao
        }

        return artesaoAtualizado
    }

    fun excluirArtesao(id: Long): Boolean {
        val artesaoExiste = _artesaoList.value.any { it.id == id }
        if (!artesaoExiste) return false

        _artesaoList.value = _artesaoList.value.filter { it.id != id }
        return true
    }

    // Product CRUD methods
    fun adicionarProduto(
        produto: Produto
    ): Produto {

        val novoProduto = produto.copy(
            id = nextProdutoId++
        )

        _produtoList.value += novoProduto

        return novoProduto
    }

    fun buscarProdutosDoArtesao(
        artesaoId: Long
    ): List<Produto> {

        return _produtoList.value.filter {
            it.artesaoId == artesaoId
        }
    }

    fun buscarProdutoPorId(
        id: Long
    ): Produto? {

        return _produtoList.value.find {
            it.id == id
        }
    }

    fun listarProdutos(): List<Produto> {
        return _produtoList.value
    }

    fun buscarProdutosPorArtesao(
        artesaoId: Long
    ): List<Produto> {

        return _produtoList.value.filter {
            it.artesaoId == artesaoId
        }
    }

    fun atualizarProduto(
        id: Long,
        dados: AtualizarProdutoDTO
    ): Produto? {

        val produtoAtual = buscarProdutoPorId(id) ?: return null

        val produtoAtualizado = produtoAtual.copy(
            nome = dados.nome,
            descricao = dados.descricao,
            preco = dados.preco
        )

        _produtoList.value =
            _produtoList.value.map { produto ->

                if (produto.id == id) {
                    produtoAtualizado
                } else {
                    produto
                }
            }
        return produtoAtualizado
    }

    fun excluirProduto(
        id: Long
    ): Boolean {

        val produtoExiste = _produtoList.value.any {
            it.id == id
        }

        if (!produtoExiste) {
            return false
        }

        _produtoList.value = _produtoList.value.filter {
            it.id != id
        }

        return true
    }

    fun baixarEstoqueProduto(
        id: Long,
        quantidade: Int = 1
    ): Produto? {

        val produtoAtual = buscarProdutoPorId(id) ?: return null

        val produtoAtualizado = produtoAtual.copy(
            quantidadeEstoque = (produtoAtual.quantidadeEstoque - quantidade).coerceAtLeast(0)
        )

        _produtoList.value = _produtoList.value.map { produto ->
            if (produto.id == id) produtoAtualizado else produto
        }

        return produtoAtualizado

    }

    // Sale crud methods
    fun adicionarVenda(
        venda: Venda
    ): Venda {

        val novaVenda = venda.copy(
            id = nextVendaId++
        )

        _vendaList.value += novaVenda

        return novaVenda

    }

    fun listarVendas(): List<Venda>
    {
        return _vendaList.value
    }

}

/*
During runtime this object will store the data as a temporary database
 */