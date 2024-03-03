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
        @SerializedName("Html") val htmlString: String?,
        @SerializedName("Itens") val items: List<Operation>
    ) {
        data class Operation(
            @SerializedName("DataPagamento") val paymentDate: String? = null,
            @SerializedName("Html") val htmlString: String? = null,
            @SerializedName("Parcelas") val installmentsNumber: Int? = null,
            @SerializedName("TipoPagamento") val paymentType: Int? = null,
            @SerializedName("TransacaoID") val transactionId: String? = null,
            @SerializedName("Valor") val value: BigDecimal? = null,
            val isHeader: Boolean? = false
        )
    }
}