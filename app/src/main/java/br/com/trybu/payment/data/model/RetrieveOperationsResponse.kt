package br.com.trybu.payment.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RetrieveOperationsResponse(
    @SerializedName("Errors") val errors: List<Error>?,
    @SerializedName("Itens") val operations: List<Operation>
) {

    data class Error(
        @SerializedName("Code") val errorCode: Any?,
        @SerializedName("Description") val errorDescription: String?,
    )

    data class Operation(
        @SerializedName("Cancelamento") val isRefund: Boolean?,
        @SerializedName("Cobranca") val isRequestPayment: Boolean?,
        @SerializedName("Html") val htmlString: String?,
        @SerializedName("Itens") val transactionsTypes: List<TransactionType>
    ) {
        data class TransactionType(
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