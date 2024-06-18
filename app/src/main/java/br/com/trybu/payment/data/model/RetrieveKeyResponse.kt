package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName

data class RetrieveKeyResponse(
    @SerializedName("Errors") val errors: List<RetrieveOperationsResponse.Error>?,
    @SerializedName("Chave") val key: String,
    @SerializedName("CNPJ") val document: String,
    @SerializedName("NomeEstabelecimento") val establismentName: String

)

data class RetrieveSessionID(
    @SerializedName("Errors") val errors: List<RetrieveOperationsResponse.Error>?,
)