package com.example.nprojetoartesanato.data.network

import com.example.nprojetoartesanato.data.network.dto.VendaRequest
import com.example.nprojetoartesanato.data.network.dto.VendaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VendaApiService {

    @POST("api/vendas")
    suspend fun registrar(
        @Body request: VendaRequest
    ): Response<VendaResponse>

    @GET("api/vendas/me")
    suspend fun listarMinhasVendas(): Response<List<VendaResponse>>
}
