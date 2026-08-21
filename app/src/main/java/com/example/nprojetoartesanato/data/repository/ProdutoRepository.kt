package com.example.nprojetoartesanato.data.repository

import com.example.nprojetoartesanato.data.local.LocalDataStore
import com.example.nprojetoartesanato.model.Produto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutoRepository {

    fun cadastrar(
        produto: Produto,
    ): Produto {

        return LocalDataStore.adicionarProduto(produto)
    }

    fun buscarPorArtesao(
        artesaoId: Long
    ): List<Produto> {

        return LocalDataStore.buscarProdutosDoArtesao(
            artesaoId
        )
    } // <-- this returns List<Produto>

    fun observarPorArtesao(
        artesaoId: Long
    ): Flow<List<Produto>> {

        return LocalDataStore.produtoList.map { produtos ->
            produtos.filter {
                it.artesaoId == artesaoId
            }
        }
    } // <-- this returns Flow<List<Produto>>

    fun buscarPorId(
        id: Long
    ): Produto? {

        return LocalDataStore.buscarProdutoPorId(id)
    }

    fun listarTodos(): List<Produto> {
        return LocalDataStore.listarProdutos()
    }

    fun atualizar(
        id: Long,
        //dados: AtualizarProdutoDTO
    ): Produto? {

        return LocalDataStore.atualizarProduto(
            id,
            //dados
        )
    }

    fun excluir(
        id: Long
    ): Boolean{
        return LocalDataStore.excluirProduto(id)
    }
}