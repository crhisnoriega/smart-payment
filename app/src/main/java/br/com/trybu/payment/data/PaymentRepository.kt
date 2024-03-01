package br.com.trybu.payment.data

import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.data.model.RetrieveKeyRequest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val smartPaymentAPI: SmartPaymentAPI
) {

    suspend fun retrieveKey(serialNumber: String) = flow {
        val response =
            smartPaymentAPI.retrieveKey(
                RetrieveKeyRequest(
                    serialNumber = serialNumber,
                    secret = "093nmaASDF1223jkjmmdsSDS#3238ujoklasdkljoiu33000---23lkmls,dmf"
                )
            )
        emit(response.body()?.key)
    }
}