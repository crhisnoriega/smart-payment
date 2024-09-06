package br.com.trybu.payment.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.postDelayed
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.api.Resources
import br.com.trybu.payment.api.safeAPICall
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.db.TransactionDao
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
import br.com.trybu.payment.util.TransactionException
import br.com.trybu.payment.util.sanitizeToSend
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

private val TRANSACTION_FINAL_STATES = listOf(18, 19)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val plugPag: PlugPag,
    private val transactionDao: TransactionDao,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository
) : ViewModel() {

    var uiState by mutableStateOf(UIState.PaymentData())
    private var _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var transactionFinished = false


    fun doPayment(
        operation: RetrieveOperationsResponse.Operation.TransactionType,
        sessionID: String
    ) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (uiState.currentTransactionId != null) return@launch
                uiState =
                    uiState.copy(
                        currentTransactionId = operation.transactionId,
                        paymentState = "PROCESSANDO"
                    )
                transactionFinished = false

                if (plugPag.isServiceBusy()) {
                    abort()
                }

                var transaction = Transaction(
                    id = operation.transactionId ?: "",
                    createDate = currentDate(),
                    lastUpdate = currentDate(),
                    status = Status.CREATED,
                    transactionStatus = TransactionStatus.NONE,
                    transactionType = TransactionType.PAYMENT,
                    sessionID = sessionID,
                    originOperationJson = Gson().toJson(operation)
                )
                transactionDao.insertOrUpdateTransaction(transaction)

                plugPag.setEventListener(object : PlugPagEventListener {
                    override fun onEvent(data: PlugPagEventData) {
                        Log.i(
                            "log",
                            "eventCode: ${data.eventCode} customMessage: ${data.customMessage}"
                        )
                        if (transactionFinished) return

                        uiState = when (data.eventCode) {
                            in TRANSACTION_FINAL_STATES -> {
                                transactionFinished = true
                                uiState.copy(
                                    paymentState = data.customMessage
                                )
                            }

                            else -> uiState.copy(paymentState = data.customMessage)
                        }
                    }
                })

                plugPag.setPlugPagCustomPrinterLayout(getCustomPrinterDialog())

                // chamada sdk PagSeguro
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
                        isCarne = false,
                    )
                )

                // update ui
                updateUIStateWithResult(plugPagResult)

                // update transaction
                transaction =
                    transaction.copy(
                        transactionStatus = if (plugPagResult.result != 0) TransactionStatus.REJECTED else TransactionStatus.APPROVED,
                        jsonTransaction = Gson().toJson(plugPagResult),
                    )

                // persist transaction with status
                updateTransactionAsStatus(transaction, Status.PROCESSED)

                // send transaction
                sendAndPersistTransaction(transaction)
            } catch (e: Exception) {
                sendAbort(operation.transactionId ?: "", sessionID)
            }
        }


    fun doRefund(transactionId: String?) = CoroutineScope(Dispatchers.IO).launch {

        try {
            uiState =
                uiState.copy(
                    currentTransactionId = transactionId,
                    paymentState = "PROCESSANDO"
                )

            var transaction = transactionDao.findTransaction(transactionId ?: "")
                ?: throw TransactionException("Dados para estorno não encontrados no terminal")
            val result =
                Gson().fromJson(transaction.jsonTransaction, PlugPagTransactionResult::class.java)

            transaction = transaction.copy(transactionType = TransactionType.REFUND)

            if (plugPag.isServiceBusy()) {
                abort()
            }

            uiState =
                uiState.copy(paymentState = "PROCESSANDO")

            plugPag.setEventListener(object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    Log.i(
                        "log", "eventCode: ${data.eventCode} customMessage: ${data.customMessage}"
                    )
                    if (transactionFinished) return

                    uiState = when (data.eventCode) {
                        in TRANSACTION_FINAL_STATES -> {
                            transactionFinished = true
                            uiState.copy(
                                paymentState = data.customMessage
                            )
                        }

                        else -> uiState.copy(paymentState = data.customMessage)
                    }
                }
            })

            val plugPagResultRefund = plugPag.voidPayment(
                PlugPagVoidData(
                    transactionId = result.transactionId ?: "",
                    transactionCode = result.transactionCode ?: ""
                )
            )

            updateUIStateWithResult(plugPagResultRefund)

            transaction =
                transaction.copy(
                    transactionStatus = if (plugPagResultRefund.result != 0) TransactionStatus.REJECTED else TransactionStatus.APPROVED,
                    jsonTransaction = Gson().toJson(plugPagResultRefund),
                    transactionType = TransactionType.REFUND
                )

            updateTransactionAsStatus(transaction, Status.PROCESSED)

            sendTransactionResult(transaction, isRefund = true)
        } catch (e: TransactionException) {
            uiState = uiState.copy(paymentState = e.message)
        } catch (e: Exception) {
            uiState = uiState.copy(paymentState = "Erro inesperado, por favor tente mais tarde")
        }
    }

    private fun updateUIStateWithResult(result: PlugPagTransactionResult) {
        uiState = uiState.copy(
            paymentState = if (result.result != 0) result.message?.uppercase() else null,
            currentTransactionId = null
        )
    }

    private suspend fun sendAndPersistTransaction(
        transaction: Transaction
    ) {
        when (transaction.transactionStatus) {
            TransactionStatus.APPROVED, TransactionStatus.REJECTED -> sendTransactionResult(transaction)
//            TransactionStatus.REJECTED -> {
//                sendAbort(transaction.id, transaction.sessionID)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    stopServiceAndGoBack()
//                }, 1000)
 //           }

            else -> updateTransactionAsStatus(transaction, Status.ERROR_ACK)
        }
    }

    private suspend fun sendAbort(transactionId: String, sessionID: String) =
        CoroutineScope(Dispatchers.IO).launch {
            safeAPICall {
                paymentRepository.abortPayment(
                    transactionId = transactionId,
                    key = keyRepository.retrieveKey(),
                    sessionID = sessionID
                )
            }.collect {
                when (it) {
                    is Resources.Success<*> -> {
                        transactionDao.findTransaction(transactionId)
                            ?.let { transaction ->
                                updateTransactionAsStatus(transaction, Status.ACK_SEND)
                            }
                    }

                    else -> {}
                }
            }
        }

    private suspend fun sendTransactionResult(
        transaction: Transaction,
        isRefund: Boolean? = false
    ) {
        uiState = uiState.copy(paymentState = "ENVIANDO...")
        safeAPICall {
            if (isRefund == false) {
                paymentRepository.confirmPayment(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction.sanitizeToSend(),
                    key = keyRepository.retrieveKey() ?: "",
                    sessionID = transaction.sessionID
                )
            } else {
                paymentRepository.confirmRefund(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction.sanitizeToSend(),
                    key = keyRepository.retrieveKey() ?: ""
                )
            }
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    updateTransactionAsStatus(transaction, Status.ACK_SEND)
                    uiState = uiState.copy(paymentState = "ENVIADO COM SUCESSO")
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopServiceAndGoToInformation()
                    }, 1000)
                }

                is Resources.Error -> {
                    updateTransactionAsStatus(transaction, Status.ERROR_ACK)
                    uiState = uiState.copy(paymentState = "ERRO NO ENVIO")
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
        transactionDao.insertOrUpdateTransaction(transactionEntity)
    }

    private fun stopServiceAndGoBack() {
        plugPag.disposeSubscriber()
        plugPag.unbindService()
        runBlocking { _uiEvent.send(UIEvent.GoToBack) }
    }

    private fun stopServiceAndGoToInformation() {
        plugPag.disposeSubscriber()
        plugPag.unbindService()
        runBlocking { _uiEvent.send(UIEvent.GoToInformation) }
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
            _uiEvent.send(UIEvent.GoToInformation)
        }
    }
}