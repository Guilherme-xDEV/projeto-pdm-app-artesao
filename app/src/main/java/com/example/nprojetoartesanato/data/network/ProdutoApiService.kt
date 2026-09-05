package com.example.nprojetoartesanato.data.network

import com.example.nprojetoartesanato.data.network.dto.ProdutoCreateDTO
import com.example.nprojetoartesanato.data.network.dto.ProdutoResponse
import com.example.nprojetoartesanato.data.network.dto.ProdutoUpdateDTO
import retrofit2.Response
import retrofit2.http.*

interface ProdutoApiService {

    @POST("api/produtos")
    suspend fun criar(
        @Body dto: ProdutoCreateDTO
    ): Response<ProdutoResponse>

    @GET("api/produtos")
    suspend fun listarMeusProdutos(): Response<List<ProdutoResponse>>

    @GET("api/produtos/{id}")
    suspend fun buscarPorId(
        @Path("id") id: Long
    ): Response<ProdutoResponse>

    @PATCH("api/produtos/{id}")
    suspend fun atualizar(
        @Path("id") id: Long,
        @Body dto: ProdutoUpdateDTO
    ): Response<ProdutoResponse>

    @DELETE("api/produtos/{id}")
    suspend fun deletar(
        @Path("id") id: Long
    ): Response<Void>
}
