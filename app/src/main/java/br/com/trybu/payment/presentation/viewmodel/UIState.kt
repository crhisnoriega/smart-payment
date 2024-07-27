package br.com.trybu.payment.presentation.viewmodel

import br.com.trybu.payment.data.model.RetrieveOperationsResponse

sealed class UIState(
    val error: String? = null,
    val paymentState: String? = null,
    val currentTransactionId: String? = null,
    var transactionType: RetrieveOperationsResponse.Operation.TransactionType? = null,
    var isRefund: String? = null,
    var sessionID: String? = null,
) {
    data object Nothing : UIState()
    class InitializeSuccess(
        val establishmentName: String? = null,
        val establishmentDocument: String? = null,
    ) : UIState()

    data object InitializeFail : UIState()
    class OperationList(val operations: List<RetrieveOperationsResponse.Operation>?) : UIState()
    data object EmptyList : UIState()
    data object LoadingList : UIState()
    class ErrorOperations(error: String?) : UIState()
    class ErrorGoToPayment(errors: List<RetrieveOperationsResponse.Error>?) : UIState()
    class TryPayment(
        transactionType: RetrieveOperationsResponse.Operation.TransactionType,
        isRefund: String?,
        sessionID: String
    ) : UIState()

}


