package br.com.trybu.payment.viewmodel

import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import br.com.trybu.payment.util.PaymentConstants.INSTALLMENT_TYPE_A_VISTA
import br.com.trybu.payment.util.PaymentConstants.TYPE_CREDITO
import br.com.trybu.payment.util.PaymentConstants.USER_REFERENCE
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.TerminalCapabilities
import com.google.gson.Gson
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

    var operations by mutableStateOf<List<RetrieveOperationsResponse.Items.Operation>>(listOf())


    init {
        viewModelScope.launch {
            if (keyRepository.retrieveKey().isNullOrBlank()) {
                retrieveKey()
            }
        }
    }

    fun retrieveOperations(document: String) = viewModelScope.launch {
        paymentRepository.retrieveOperations(keyRepository.retrieveKey(), document)
            .catch {

            }.collect {
                operations = it ?: listOf()
            }
    }

    fun retrieveKey() = viewModelScope.launch {
        paymentRepository.retrieveKey(serialNumber = Build.SERIAL)
            .catch {
            }.collect { key ->
                Log.i("log", "key: $key")
                key?.let { keyRepository.persisKey(it) }
            }
    }

    fun doPayment(operation: RetrieveOperationsResponse.Items.Operation) =
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("log", "isAuthenticated: ${plugPag.isAuthenticated()}")

            plugPag.setEventListener(object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    Log.i("log", "eventCode: ${data.eventCode} customMessage: ${data.customMessage}")
                }
            })
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
            Log.i("log", "result: ${result}")
        }
}