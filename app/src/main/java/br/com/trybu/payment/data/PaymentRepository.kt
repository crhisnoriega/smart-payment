package br.com.trybu.payment.data

import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.data.model.RetrieveKeyRequest
import br.com.trybu.payment.data.model.RetrieveOperationsRequest
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
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

    suspend fun retrieveOperations(key: String?, document: String?) = flow {
        val response = smartPaymentAPI.retrieveOperations(
            RetrieveOperationsRequest(
                key = key,
                textQuery = document
            )
        )

        val joined = mutableListOf<RetrieveOperationsResponse.Items.Operation>()
        response.body()?.items?.forEach {
            joined.addAll(it.items)
        }

        emit(joined)
    }
}