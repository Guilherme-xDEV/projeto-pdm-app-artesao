package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Produto

class ProdutoRepository {

    fun cadastrar(
        produto: Produto
    ): Produto {

        return LocalDataStore.adicionarProduto(produto)
    }

    fun buscarPorArtesao(
        artesaoId: Long
    ): List<Produto> {

        return LocalDataStore.buscarProdutosDoArtesao(
            artesaoId
        )
    }
}

/*

CadastroProdutoScreen
        │
        │ viewModel.cadastrar()
        ▼
CadastroProdutoViewModel
        │
        ├── verifica artesão autenticado
        │
        ├── valida nome
        ├── valida descrição
        ├── valida preço
        ├── valida estoque
        │
        ▼
      Produto
        │
        │ artesaoId = SessionManager.artesaoAtual.id
        ▼
ProdutoRepository
        │
        ▼
LocalDataStore

 */