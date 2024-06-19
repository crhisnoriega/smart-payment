package br.com.trybu.payment.presentation.viewmodel

import br.com.trybu.payment.data.model.RetrieveOperationsResponse

data class UIState(
    val operations: List<RetrieveOperationsResponse.Operation>? = null,
    val error: String? = null,
    val paymentState: String? = null,
    val isLoading: Boolean? = false,
    val currentTransactionId: String? = null,
    val wasInitialized: Boolean? = null,
    val establishmentName: String? = null,
    val establishmentDocument: String? = null,
    val serialNumber: String? = null,
    var showInfo: InitializationStatus? = InitializationStatus.ShowNothing,
    var transactionType: RetrieveOperationsResponse.Operation.TransactionType? = null,
    var isRefund: String? = null,
    var errors: List<RetrieveOperationsResponse.Error>? = null,
    var sessionID: String? = null

)

sealed class InitializationStatus() {
    data object ShowInfo : InitializationStatus()
    data object ShowPending : InitializationStatus()
    data object ShowNothing : InitializationStatus()
}