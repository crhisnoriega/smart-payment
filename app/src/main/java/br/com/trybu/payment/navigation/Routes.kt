package br.com.trybu.payment.navigation

object Routes {

    const val main = "payments"

    object Payment {
        const val initialize = "initialize"
        const val information = "information"
        const val operations = "operations/{query}"
        const val details = "details/{isRefund}/{operation}"
    }

    val payment = Payment
}