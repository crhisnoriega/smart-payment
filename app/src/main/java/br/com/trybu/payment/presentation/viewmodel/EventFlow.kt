package br.com.trybu.payment.presentation.viewmodel

sealed class EventFlow {
    data object GoToBack : EventFlow()
    data object GoToInitialization : EventFlow()
    data object GoToInformation : EventFlow()
    data object GoToListing : EventFlow()
    data object None : EventFlow()
}