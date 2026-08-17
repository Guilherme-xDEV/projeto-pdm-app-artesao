package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutoRepository {

    fun cadastrar(
        produto: Produto
    ): Produto {

        return LocalDataStore.adicionarProduto(produto)
    }

//    fun buscarPorArtesao(
//        artesaoId: Long
//    ): List<Produto> {
//
//        return LocalDataStore.buscarProdutosDoArtesao(
//            artesaoId
//        )
//    } // <-- this returns List<Produto>

    fun observarPorArtesao(
        artesaoId: Long
    ): Flow<List<Produto>> {

        return LocalDataStore.produtoList.map { produtos ->
            produtos.filter {
                it.artesaoId == artesaoId
            }
        }
    } // <-- this returns Flow<List<Produto>>
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