package com.example.nprojetoartesanato.data.local

import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalDataStore {

    private val artesaoList = mutableListOf<Artesao>()
    private val _produtoList = MutableStateFlow<List<Produto>>(emptyList())
    val produtoList: StateFlow<List<Produto>> = _produtoList.asStateFlow()

    private var nextArtesaoId = 1L
    private var nextProdutoId = 1L

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
}

/*
During runtime this object will store the data as a temporary database
 */