package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName

data class PaymentConfirmRequest(
    @SerializedName("TransacaoID") val transactionId: String,
    @SerializedName("RetornoPOS") val jsonRaw: String,
    @SerializedName("Chave") val key: String

)