package br.com.trybu.payment.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.util.PaymentConstants.INSTALLMENT_TYPE_A_VISTA
import br.com.trybu.payment.util.PaymentConstants.TYPE_CREDITO
import br.com.trybu.payment.util.PaymentConstants.USER_REFERENCE
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.data.request.PlugPagBeepData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


private val TRANSACTION_FINAL_STATES = listOf(18, 19)


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val plugPag: PlugPag
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

            // handle json  result
            state = if (result.result != 0) {
                state.copy(
                    paymentState = result.message?.uppercase(),
                    currentTransactionId = null
                )
            } else {
                state.copy(currentTransactionId = null)
            }

            plugPag.disposeSubscriber()
            plugPag.unbindService()
        }
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


}