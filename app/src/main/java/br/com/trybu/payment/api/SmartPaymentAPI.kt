package br.com.trybu.payment.api

import br.com.trybu.payment.data.model.ConfirmRequest
import br.com.trybu.payment.data.model.RetrieveKeyRequest
import br.com.trybu.payment.data.model.RetrieveKeyResponse
import br.com.trybu.payment.data.model.RetrieveOperationsRequest
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
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
    suspend fun retrieveOperations(
        @Body retrieveOperationsRequest: RetrieveOperationsRequest
    ): Response<RetrieveOperationsResponse>

    @POST("ConfirmarPagamentoPOS")
    @Headers("Content-Type: application/json")
    suspend fun paymentConfirm(@Body paymentConfirmRequest: ConfirmRequest): Response<JsonElement>

    @POST("ConfirmarCancelamentoPOS")
    @Headers("Content-Type: application/json")
    suspend fun refundConfirm(@Body paymentConfirmRequest: ConfirmRequest): Response<JsonElement>
}