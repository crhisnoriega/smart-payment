package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName

data class RetrieveKeyRequest(
    @SerializedName("Secret") val secret: String,
    @SerializedName("SerialNumber") val serialNumber: String
)