package com.example.nprojetoartesanato.data.local

import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.Produto
import com.example.nprojetoartesanato.model.dto.AtualizarProdutoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalDataStore {

    private val artesaoList = mutableListOf<Artesao>()
    private val _produtoList = MutableStateFlow<List<Produto>>(emptyList())
    val produtoList: StateFlow<List<Produto>> = _produtoList.asStateFlow()

    private var nextArtesaoId = 1L
    private var nextProdutoId = 1L

    // Artisan CRUD Methods
    fun adicionarArtesao(
        artesao: Artesao
    ): Artesao {

        val novoArtesao = artesao.copy(
            id = nextArtesaoId++
        )

        artesaoList.add(novoArtesao)
        return novoArtesao
    }

    fun buscarArtesao(
        usuario: String,
        senha: String
    ): Artesao? {

        return artesaoList.find {
            it.usuario == usuario &&
            it.senha == senha
        }
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

}

/*
During runtime this object will store the data as a temporary database
 */