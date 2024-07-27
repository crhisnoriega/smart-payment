package br.com.trybu.payment.navigation

object Routes {

    const val main = "payments"

    object Payment {
        const val initialize = "initialize"
        const val information = "information/{state}"
        const val operations = "operations/{query}"
        const val details = "details/{isRefund}/{operation}/{sessionID}"
        const val pending = "pending"
    }

    val payment = Payment
}