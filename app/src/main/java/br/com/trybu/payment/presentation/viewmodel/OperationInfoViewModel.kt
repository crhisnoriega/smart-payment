package br.com.trybu.payment.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import br.com.trybu.payment.worker.CheckTransactionsWorker
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class OperationInfoViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,

    ) : ViewModel() {

    var uiState = MutableLiveData<String>()
    var qrCode = MutableLiveData<String>()
    var state by mutableStateOf(UIState(operations = listOf()))


    fun retrieveOperations(document: String) = viewModelScope.launch {
        state = state.copy(operations = listOf(), isLoading = true, error = null)
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
                        operations = null,
                        isLoading = false
                    )

                is Resources.Loading -> {

                }
            }

            qrCode.value = ""
        }
    }

    fun retrieveKey() = viewModelScope.launch {
        paymentRepository.retrieveKey(serialNumber = Build.SERIAL)
            .collect { establishment ->
                if (establishment?.errors?.isEmpty() == true) {
                    establishment.key.let { keyRepository.persisKey(it) }
                    state = state.copy(
                        wasInitialized = true,
                        establishmentName = establishment.establismentName,
                        establishmentDocument = establishment.document,
                        serialNumber = Build.SERIAL,
                        showInfo = true
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        state = state.copy(showInfo = false)
                    }, 3000)

                    CheckTransactionsWorker.startWorker(context)
                } else {
                    state = state.copy(
                        wasInitialized = false,
                        error = establishment?.errors?.first()?.errorDescription
                    )
                }
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

    fun exit() {
        uiState.postValue("exit")
    }

    fun qrCode(contents: String) {
        qrCode.value = Uri.encode(contents)
    }

    fun tryGoToPayment(
        operation: RetrieveOperationsResponse.Operation.TransactionType,
        isRefund: String?
    ) =
        viewModelScope.launch {
            paymentRepository.startPayment(
                transactionId = operation.transactionId,
                key = keyRepository.retrieveKey(),
                sessionID = UUID.randomUUID().toString()
            ).collect {
                if (it.body()?.errors?.isEmpty() == false) {
                    state = state.copy(errors = it.body()?.errors)
                } else {
                    state = state.copy(transactionType = operation, isRefund = isRefund)
                }

                return@collect
            }


        }
}
