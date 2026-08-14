package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Produto

class ProdutoRepository {

    fun cadastrar(
        produto: Produto
    ): Produto {

        return LocalDataStore.adicionarProduto(produto)
    }

    fun buscarProduto(
        produto: Produto
    ): List<Produto> {

        return LocalDataStore.buscarProdutosDoArtesao(
            produto.artesaoId // verify if this works!
        )
    }
}