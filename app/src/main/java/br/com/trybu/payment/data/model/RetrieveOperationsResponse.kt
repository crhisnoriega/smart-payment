package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RetrieveOperationsResponse(
    @SerializedName("Errors") val errors: List<Any>,
    @SerializedName("Itens") val items: List<Items>
) {

    data class Items(
        @SerializedName("Cancalamento") val isCancel: Boolean?,
        @SerializedName("Cobranca") val isPayment: Boolean?,
        @SerializedName("Itens") val items: List<Operation>
    ) {
        data class Operation(
            @SerializedName("DataPagamento") val paymentDate: String?,
            @SerializedName("Html") val htmlString: String?,
            @SerializedName("Parcelas") val installmentsNumber: Int?,
            @SerializedName("TipoPagamento") val paymentType: Int?,
            @SerializedName("TransacaoID") val transactionId: String?,
            @SerializedName("Valor") val value: BigDecimal?,
        )
    }
}