package br.com.trybu.payment.presentation.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.api.Resources
import br.com.trybu.payment.api.safeAPICall
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.db.TransactionDB
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction
import br.com.trybu.payment.db.entity.TransactionStatus
import br.com.trybu.payment.db.entity.TransactionType
import br.com.trybu.payment.db.entity.currentDate
import br.com.trybu.payment.util.PaymentConstants.INSTALLMENT_TYPE_A_VISTA
import br.com.trybu.payment.util.PaymentConstants.INSTALLMENT_TYPE_PARC_VENDEDOR
import br.com.trybu.payment.util.PaymentConstants.TYPE_CREDITO
import br.com.trybu.payment.util.PaymentConstants.TYPE_DEBITO
import br.com.trybu.payment.util.PaymentConstants.USER_REFERENCE
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


private val TRANSACTION_FINAL_STATES = listOf(18, 19)


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val plugPag: PlugPag,
    private val transactionDB: TransactionDB,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository
) : ViewModel() {

    var uiState = MutableLiveData<String>()
    var state by mutableStateOf(UIState(operations = listOf()))
    private var transactionFinished = false

    private fun updateStateWithResult(result: PlugPagTransactionResult) {
        state = state.copy(
            paymentState = if (result.result != 0) result.message?.uppercase() else null,
            currentTransactionId = null
        )
    }

    private suspend fun sendAndPersistTransaction(
        transaction: Transaction
    ) {
        when (transaction.transactionStatus) {
            TransactionStatus.APPROVED -> {
                sendTransactionResult(transaction)
            }

            else -> {
                transactionDB.transactionDao().insertOrUpdateTransaction(transaction)
            }
        }
    }

    private suspend fun sendTransactionResult(
        transaction: Transaction,
        isRefund: Boolean? = false
    ) {
        state = state.copy(paymentState = "ENVIANDO...")
        safeAPICall {
            if (isRefund == false) {
                paymentRepository.confirmPayment(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction ?: "",
                    key = keyRepository.retrieveKey() ?: ""
                )
            } else {
                paymentRepository.confirmRefund(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction ?: "",
                    key = keyRepository.retrieveKey() ?: ""
                )
            }
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    updateTransactionAsStatus(transaction, Status.ACK_SEND)
                    state = state.copy(paymentState = "ENVIADO COM SUCESSO")
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopServiceAndGoBack()
                    }, 5000)
                }

                is Resources.Error -> {
                    updateTransactionAsStatus(transaction, Status.ERROR_SEND)
                    state = state.copy(paymentState = "ERRO NO ENVIO")
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopServiceAndGoBack()
                    }, 1000)
                }

                else -> {}
            }
        }
    }

    private fun updateTransactionAsStatus(
        transaction: Transaction,
        status: Status
    ) {

        val transactionEntity = transaction.copy(
            jsonTransaction = transaction.jsonTransaction,
            lastUpdate = currentDate(),
            status = status
        )
        transactionDB.transactionDao().insertOrUpdateTransaction(transactionEntity)
    }

    private fun stopServiceAndGoBack() {
        uiState.postValue("goback")
        plugPag.disposeSubscriber()
        plugPag.unbindService()
    }


    private fun getCustomPrinterDialog(): PlugPagCustomPrinterLayout {
        val customDialog = PlugPagCustomPrinterLayout()
        customDialog.title = "Impressão de comprovante"
        customDialog.maxTimeShowPopup = 60
        customDialog.buttonBackgroundColor = "#1462A6"
        customDialog.buttonBackgroundColorDisabled = "#8F8F8F"
        return customDialog
    }

    fun abort() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                plugPag.abort()
            } catch (_: IllegalArgumentException) {

            }
        }

        viewModelScope.launch {
            uiState.value = "goback"
        }
    }

    fun doPayment(operation: RetrieveOperationsResponse.Operation.TransactionType) {

        Log.i("log", "transaction: ${Gson().toJson(operation)}")

        if (state.currentTransactionId != null) return
        state =
            state.copy(currentTransactionId = operation.transactionId, paymentState = "PROCESSANDO")
        transactionFinished = false

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("log", "start: ${operation.transactionId}")

            paymentRepository.startPayment(
                operation.transactionId,
                key = keyRepository.retrieveKey()
            ).collect {
                Log.i("log", "start payment")
            }

            if (plugPag.isServiceBusy()) {
                abort()
            }

            var transaction = Transaction(
                id = operation.transactionId ?: "",
                createDate = currentDate(),
                lastUpdate = currentDate(),
                status = Status.CREATED,
                transactionStatus = TransactionStatus.NONE,
                transactionType = TransactionType.PAYMENT
            )
            transactionDB.transactionDao().insertOrUpdateTransaction(transaction)

            plugPag.setEventListener(object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    Log.i(
                        "log", "eventCode: ${data.eventCode} customMessage: ${data.customMessage}"
                    )
                    if (transactionFinished) return

                    state = when (data.eventCode) {
                        in TRANSACTION_FINAL_STATES -> {
                            transactionFinished = true
                            state.copy(
                                paymentState = data.customMessage
                            )
                        }

                        else -> state.copy(paymentState = data.customMessage)
                    }
                }
            })

            plugPag.setPlugPagCustomPrinterLayout(getCustomPrinterDialog())

            val plugPagResult = plugPag.doPayment(
                PlugPagPaymentData(
                    type = when (operation.paymentType) {
                        2 -> TYPE_CREDITO
                        4 -> TYPE_DEBITO
                        else -> -1
                    },
                    amount = operation.value?.multiply(BigDecimal(100))?.toInt() ?: -1,
                    installmentType = if (operation.installmentsNumber == 1) INSTALLMENT_TYPE_A_VISTA else INSTALLMENT_TYPE_PARC_VENDEDOR,
                    installments = operation.installmentsNumber ?: 1,
                    userReference = USER_REFERENCE,
                    printReceipt = false,
                    partialPay = false,
                    isCarne = false
                )
            )

            updateStateWithResult(plugPagResult)

            transaction =
                transaction.copy(
                    transactionStatus = if (plugPagResult.result != 0) TransactionStatus.REJECTED else TransactionStatus.APPROVED,
                    jsonTransaction = Gson().toJson(plugPagResult).replace("\\u", ""),
                )

            updateTransactionAsStatus(transaction, Status.PROCESSED)

            sendAndPersistTransaction(transaction)
        }
    }


    fun doRefund(transactionId: String?) = CoroutineScope(Dispatchers.IO).launch {

        try {
            var transaction = transactionDB.transactionDao().findTransaction(transactionId ?: "")
                ?: throw Exception("don't found local transaction")
            val result =
                Gson().fromJson(transaction.jsonTransaction, PlugPagTransactionResult::class.java)

            transaction = transaction.copy(transactionType = TransactionType.REFUND)

            if (plugPag.isServiceBusy()) {
                abort()
            }

            state =
                state.copy(paymentState = "PROCESSANDO")

            plugPag.setEventListener(object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    Log.i(
                        "log", "eventCode: ${data.eventCode} customMessage: ${data.customMessage}"
                    )
                    if (transactionFinished) return

                    state = when (data.eventCode) {
                        in TRANSACTION_FINAL_STATES -> {
                            transactionFinished = true
                            state.copy(
                                paymentState = data.customMessage
                            )
                        }

                        else -> state.copy(paymentState = data.customMessage)
                    }

                }
            })

            val plugPagResultRefund = plugPag.voidPayment(
                PlugPagVoidData(
                    transactionId = result.transactionId ?: "",
                    transactionCode = result.transactionCode ?: ""
                )
            )

            updateStateWithResult(plugPagResultRefund)

            transaction =
                transaction.copy(
                    transactionStatus = if (plugPagResultRefund.result != 0) TransactionStatus.REJECTED else TransactionStatus.APPROVED,
                    jsonTransaction = Gson().toJson(plugPagResultRefund).replace("\\u", ""),
                    transactionType = TransactionType.REFUND
                )

            updateTransactionAsStatus(transaction, Status.PROCESSED)

            sendTransactionResult(transaction, isRefund = true)
        } catch (e: Exception) {
            Log.i("log", e.message, e)
            state = state.copy(paymentState = "Erro inesperado, por favor tente mais tarde")
        }
    }
}