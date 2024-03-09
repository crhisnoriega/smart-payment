package br.com.trybu.payment.presentation.viewmodel

import br.com.trybu.payment.data.model.RetrieveOperationsResponse

data class UIState(
    val operations: List<RetrieveOperationsResponse.Items.Operation>,
    val error: String? = null,
    val paymentState: String? = null,
    val isLoading: Boolean? = false,
    val currentTransactionId: String? = null,
    val wasInitialized: Boolean = false,
    val establishmentName: String? = null,
    val establishmentDocument: String? = null,
    val serialNumber: String? = null,
    val showInfo: Boolean = false,
)