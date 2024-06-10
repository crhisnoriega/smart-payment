package br.com.trybu.payment.data.model

import android.app.blob.BlobStoreManager.Session
import com.google.gson.annotations.SerializedName

data class ConfirmRequest(
    @SerializedName("TransacaoID") val transactionId: String,
    @SerializedName("RetornoPOS") val jsonRaw: String?,
    @SerializedName("Sessao") val sessionID: String? = null,
    @SerializedName("Chave") val key: String

)