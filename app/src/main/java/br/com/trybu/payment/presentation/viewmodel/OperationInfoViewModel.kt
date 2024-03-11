package br.com.trybu.payment.presentation.viewmodel

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
class OperationInfoViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,

    ) : ViewModel() {

    var uiState = MutableLiveData<String>()
    var qrCode = MutableLiveData<String>()
    var state by mutableStateOf(UIState(operations = listOf()))


    fun retrieveOperations(document: String) = viewModelScope.launch {
        state = state.copy(operations = listOf(), isLoading = true)
        safeAPICall {
            paymentRepository.retrieveOperations(
                keyRepository.retrieveKey(), document
            )
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    val newOperations = it.data as List<RetrieveOperationsResponse.Operation>
                    state = state.copy(
                        operations = if (newOperations.isEmpty()) null else newOperations,
                        isLoading = false
                    )
                }

                is Resources.Error -> state =
                    state.copy(
                        error = it.error.message,
                        currentTransactionId = null,
                        operations = null
                    )

                is Resources.Loading -> {

                }
            }

            qrCode.value = ""
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
                    serialNumber = "PBA1238673598",
                    showInfo = true
                )

                Handler(Looper.getMainLooper()).postDelayed({
                    state = state.copy(showInfo = false)
                }, 3000)
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


    fun dismissError() {
        state = state.copy(error = null, paymentState = null)
    }

    private fun successPayment(): UIState {
        return state
    }

    fun initialized() {
        state = state.copy(wasInitialized = false)
    }

    fun openCamera() {
        uiState.value = "qrcode"
    }

    fun hideInfo() {
        state = state.copy(showInfo = false)
    }

    fun qrCode(contents: String) {
        qrCode.value = contents
    }
}
