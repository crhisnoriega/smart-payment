package br.com.trybu.payment.data

import br.com.trybu.payment.api.SmartPaymentAPI
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val smartPaymentAPI: SmartPaymentAPI
) {
}