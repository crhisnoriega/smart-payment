package br.com.trybu.payment.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.api.Resources
import br.com.trybu.payment.api.safeAPICall
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,
    private val plugPag: PlugPag
) : ViewModel() {

    var state by mutableStateOf(UIState(operations = listOf()))
    private var transactionFinished = false
    fun retrieveOperations(document: String) = viewModelScope.launch {
        state = state.copy(operations = listOf(), isLoading = true)
        safeAPICall {
            paymentRepository.retrieveOperations(
                keyRepository.retrieveKey(), document
            )
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    val newOperations = it.data as List<RetrieveOperationsResponse.Items.Operation>
                    state = state.copy(operations = newOperations, isLoading = false)
                }

                is Resources.Error -> state =
                    state.copy(error = it.error.message, currentTransactionId = null)

                is Resources.Loading -> {

                }
            }
        }
    }

    fun retrieveKey() = viewModelScope.launch {
        paymentRepository.retrieveKey(serialNumber = "PBA1238673598").catch {}
            .collect { establishment ->
                establishment?.key?.let { keyRepository.persisKey(it) }
                state = state.copy(
                    wasInitialized = true,
                    establishmentName = establishment?.establismentName,
                    establishmentDocument = establishment?.document,
                )
            }
    }

    private fun getCustomPrinterDialog(): PlugPagCustomPrinterLayout {
        val customDialog = PlugPagCustomPrinterLayout()
        customDialog.title = "Imprissão de comprovante"
        customDialog.maxTimeShowPopup = 60
        customDialog.buttonBackgroundColor = "#1462A6"
        customDialog.buttonBackgroundColorDisabled = "#8F8F8F"
        return customDialog
    }


    fun doPayment(operation: RetrieveOperationsResponse.Items.Operation) {
        if (state.currentTransactionId != null) return
        state = state.copy(currentTransactionId = operation.transactionId)
        transactionFinished = false

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("log", "start: ${operation.transactionId}")

            plugPag.beep(PlugPagBeepData(PlugPagBeepData.FREQUENCE_LEVEL_0, 1))
            plugPag.setEventListener(object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    Log.i(
                        "log", "eventCode: ${data.eventCode} customMessage: ${data.customMessage}"
                    )
                    if (transactionFinished == true) return
                    state = when (data.eventCode) {
                        4 -> {
                            transactionFinished = true
                            state.copy(paymentState = null)
                        }
                        18 -> successPayment()
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

            if (result.result != 0) {
                state = state.copy(
                    paymentState = result.message,
                    currentTransactionId = null
                )
            } else {
                state =
                    state.copy(paymentState = "Transação aprovada", currentTransactionId = null)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                state = state.copy(
                    paymentState = null,
                    currentTransactionId = null
                )
            }, 3000)

            plugPag.disposeSubscriber()
            plugPag.unbindService()
        }
    }

    fun dismissError() {
        state = state.copy(error = null, paymentState = null)
    }

    private fun successPayment(): UIState {
        return state
    }

    fun initialized() {
        state = state.copy(wasInitialized = false)
    }
}