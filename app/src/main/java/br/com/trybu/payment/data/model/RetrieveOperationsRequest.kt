package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName

data class RetrieveOperationsRequest(
    @SerializedName("Chave") val key: String?,
    @SerializedName("TextoBusca") val textQuery: String?
)