package br.com.trybu.payment.data

import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.data.model.ConfirmRequest
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
        emit(response.body())
    }

    suspend fun retrieveOperations(key: String?, document: String?) = flow {
        val response = smartPaymentAPI.retrieveOperations(
            RetrieveOperationsRequest(
                key = key,
                textQuery = document
            )
        )

        val joined = mutableListOf<RetrieveOperationsResponse.Operation.TransactionType>()
        if (response.body()?.errors?.isEmpty() == false) {
            throw Exception(response.body()?.errors?.first()?.errorDescription)
        } else {
            response.body()?.operations?.forEach {
                joined.add(
                    RetrieveOperationsResponse.Operation.TransactionType(
                        htmlString = it.htmlString,
                        isHeader = true
                    )
                )
                joined.addAll(it.transactionsTypes)
            }

            emit(response.body()?.operations)
        }
    }

    suspend fun confirmPayment(transactionId: String, jsonTransaction: String, key: String) = flow {
        val response = smartPaymentAPI.paymentConfirm(
            ConfirmRequest(
                transactionId = transactionId,
                jsonRaw = jsonTransaction,
                key = key

            )
        )
        emit(response)
    }

    suspend fun confirmRefund(transactionId: String, jsonTransaction: String, key: String) = flow {
        val response = smartPaymentAPI.refundConfirm(
            ConfirmRequest(
                transactionId = transactionId,
                jsonRaw = jsonTransaction,
                key = key
            )
        )
        emit(response)
    }

    suspend fun startPayment(transactionId: String?, key: String?, sessionID: String?) = flow {
        val response = smartPaymentAPI.startPayment(
            ConfirmRequest(
                transactionId = transactionId ?: "",
                sessionID = sessionID,
                key = key ?: "",
                jsonRaw = null
            )
        )
        emit(response)
    }

    suspend fun abortPayment(transactionId: String?, key: String?, sessionID: String?) = flow {
        val response = smartPaymentAPI.abortPayment(
            ConfirmRequest(
                transactionId = transactionId ?: "",
                sessionID = sessionID,
                key = key ?: "",
                jsonRaw = null
            )
        )
        emit(response)
    }
}