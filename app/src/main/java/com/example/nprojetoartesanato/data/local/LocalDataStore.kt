package com.example.nprojetoartesanato.data.local

import com.example.nprojetoartesanato.model.Artesao
import com.example.nprojetoartesanato.model.Produto

object LocalDataStore {

    private val artesaoList = mutableListOf<Artesao>()
    private val produtoList = mutableListOf<Produto>()

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

        produtoList.add(novoProduto)

        return novoProduto
    }

    fun buscarProdutosDoArtesao(
        artesaoId:  Long
    ): List<Produto> {

        return produtoList.filter {
            it.artesaoId == artesaoId
        }
    }
}

/*
During runtime this object will store the data as a temporary database
 */