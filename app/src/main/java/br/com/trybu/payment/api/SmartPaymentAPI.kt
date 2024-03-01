package br.com.trybu.payment.api

import br.com.trybu.payment.data.model.RetrieveKeyRequest
import br.com.trybu.payment.data.model.RetrieveKeyResponse
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartPaymentAPI {

    @Headers("Content-Type: application/json")
    @POST("InicializarPOS")
    suspend fun retrieveKey(
        @Body request: RetrieveKeyRequest
    ): Response<RetrieveKeyResponse>

    @POST("ConsultarTransacoesPOS2")
    @Headers("Content-Type: application/json")
    suspend fun retrieveOperations(): Response<JsonElement>
}