package br.com.trybu.payment.presentation.viewmodel

import br.com.trybu.payment.data.model.RetrieveOperationsResponse

data class UIState(
    val operations: List<RetrieveOperationsResponse.Items.Operation>,
    val error: String? = null,
    val paymentState: String? = null,
    val isLoading: Boolean? = false
)