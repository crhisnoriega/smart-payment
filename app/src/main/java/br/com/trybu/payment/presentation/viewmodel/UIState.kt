package br.com.trybu.payment.presentation.viewmodel

import br.com.trybu.payment.data.model.RetrieveOperationsResponse

sealed class UIState(
    open val error: String? = null,

    ) {
    data class PaymentData(
        val paymentState: String? = null,
        val currentTransactionId: String? = null,
        var transactionType: RetrieveOperationsResponse.Operation.TransactionType? = null,
        var isRefund: String? = null,
        var sessionID: String? = null,
    ) : UIState()

    data object Nothing : UIState()
    class InitializeSuccess(
        val establishmentName: String? = null,
        val establishmentDocument: String? = null,
    ) : UIState()

    data object InitializeFail : UIState()
    data class OperationList(
        val operations: List<RetrieveOperationsResponse.Operation>?,
        val isLoading: Boolean? = false,
        override val error: String? = null
    ) : UIState()

    data object EmptyList : UIState()
    data object LoadingList : UIState()
    class ErrorOperations(error: String?) : UIState()
    class ErrorGoToPayment(errors: List<RetrieveOperationsResponse.Error>?) : UIState()
    class TryPayment(
        val transactionType: RetrieveOperationsResponse.Operation.TransactionType? = null,
        val isRefund: String? = null,
        val sessionID: String? = null,
    ) : UIState()

}


