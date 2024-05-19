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
import br.com.trybu.payment.db.entity.currentDate
import br.com.trybu.payment.util.PaymentConstants.INSTALLMENT_TYPE_A_VISTA
import br.com.trybu.payment.util.PaymentConstants.TYPE_CREDITO
import br.com.trybu.payment.util.PaymentConstants.USER_REFERENCE
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagBeepData
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
    private val keyRepository: KeyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState = MutableLiveData<String>()
    var state by mutableStateOf(UIState(operations = listOf()))
    private var transactionFinished = false

    fun doPayment(operation: RetrieveOperationsResponse.Operation.TransactionType) {

        if (state.currentTransactionId != null) return
        state =
            state.copy(currentTransactionId = operation.transactionId, paymentState = "PROCESSANDO")
        transactionFinished = false

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("log", "start: ${operation.transactionId}")

            if (plugPag.isServiceBusy()) {
                abort()
            }

            var transaction = Transaction(
                id = operation.transactionId ?: "",
                createDate = currentDate(),
                lastUpdate = currentDate(),
                status = Status.CREATED
            )
            transactionDB.transactionDao().insertOrUpdateTransaction(transaction)

            plugPag.beep(PlugPagBeepData(PlugPagBeepData.FREQUENCE_LEVEL_0, 1))
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

            val result = plugPag.doPayment(
                PlugPagPaymentData(
                    type = TYPE_CREDITO,
                    amount = operation.value?.multiply(BigDecimal(100))?.toInt() ?: -1,
                    installmentType = INSTALLMENT_TYPE_A_VISTA,
                    installments = 1,
                    userReference = USER_REFERENCE,
                    printReceipt = false,
                    partialPay = false,
                    isCarne = false
                )
            )

            updateStateWithResult(result)

            transaction =
                transaction.copy(status = if (result.result != 0) Status.REJECTED else Status.APPROVED)

            persistResult(transaction, result)
        }
    }

    private fun updateStateWithResult(result: PlugPagTransactionResult) {
        state = state.copy(
            paymentState = if (result.result != 0) result.message?.uppercase() else null,
            currentTransactionId = null
        )
    }

    private suspend fun persistResult(
        transaction: Transaction,
        result: PlugPagTransactionResult
    ) {
        when (transaction.status) {
            Status.APPROVED -> sendTransactionResult(result, transaction)
            else -> transactionDB.transactionDao().insertOrUpdateTransaction(transaction)
        }
    }

    private suspend fun sendTransactionResult(
        result: PlugPagTransactionResult, transaction: Transaction
    ) {
        state = state.copy(paymentState = "ENVIANDO...")
        safeAPICall {
            paymentRepository.confirmPayment(
                transactionId = transaction.id,
                jsonTransaction = Gson().toJson(result),
                key = keyRepository.retrieveKey() ?: ""
            )
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    updateTransactionAsSuccess(transaction, result)
                    state = state.copy(paymentState = "ENVIADO COM SUCESSO")
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopServiceAndGoBack()
                    }, 5000)
                }

                is Resources.Error -> {
                    state = state.copy(paymentState = "ERRO NO ENVIO")
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopServiceAndGoBack()
                    }, 1000)
                }

                else -> {}
            }
        }
    }

    private fun updateTransactionAsSuccess(
        transaction: Transaction, result: PlugPagTransactionResult
    ) {

        val transactionEntity = transaction.copy(
            jsonTransaction = Gson().toJson(result),
            lastUpdate = currentDate(),
            status = Status.SENT_UPDATED
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

    fun doRefund(transactionId: String?) = CoroutineScope(Dispatchers.IO).launch {
        val transaction = transactionDB.transactionDao().findTransaction(transactionId ?: "")
            ?: throw Exception("don't found local transaction")
        val result =
            Gson().fromJson(transaction.jsonTransaction, PlugPagTransactionResult::class.java)

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
        val resultRefund = plugPag.voidPayment(
            PlugPagVoidData(
                transactionId = result.transactionId ?: "",
                transactionCode = result.transactionCode ?: ""
            )
        )


        updateStateWithResult(resultRefund)

        safeAPICall {
            paymentRepository.confirmRefund(
                transactionId = transaction.id,
                jsonTransaction = Gson().toJson(resultRefund),
                key = keyRepository.retrieveKey() ?: ""
            )
        }.collect {
            when (it) {
                is Resources.Success<*> -> persistResult(transaction, result)
                else -> {}
            }
        }
    }
}