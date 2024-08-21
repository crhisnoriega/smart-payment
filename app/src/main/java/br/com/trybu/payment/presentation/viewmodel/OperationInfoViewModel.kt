package br.com.trybu.payment.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
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
import br.com.trybu.payment.db.TransactionDao
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class OperationInfoViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,
    private val transactionDao: TransactionDao,
    private val plugPag: PlugPag,
) : ViewModel() {

    private var _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var _uiState by mutableStateOf<UIState>(UIState.TryPayment())







    fun retrieveOperations(document: String) = viewModelScope.launch {
        _uiState = UIState.LoadingList
        safeAPICall {
            paymentRepository.retrieveOperations(
                keyRepository.retrieveKey(), document
            )
        }.collect {
            when (it) {
                is Resources.Success<*> -> {
                    val newOperations = it.data as List<RetrieveOperationsResponse.Operation>
                    _uiState =
                        if (newOperations.isEmpty()) UIState.EmptyList else UIState.OperationList(
                            operations = newOperations
                        )
                }

                is Resources.Error -> _uiState = UIState.ErrorOperations(
                    error = it.error.message,
                )

                else -> {

                }
            }
        }
    }


    fun tryGoToPayment(
        operation: RetrieveOperationsResponse.Operation.TransactionType,
        isRefund: String?
    ) =
        viewModelScope.launch {
            val sessionID = UUID.randomUUID().toString()
            paymentRepository.startPayment(
                transactionId = operation.transactionId,
                key = keyRepository.retrieveKey(),
                sessionID = sessionID
            ).collect {
                _uiState = if (it.body()?.errors?.isEmpty() == false) {
                    UIState.ErrorGoToPayment(errors = it.body()?.errors)
                } else {
                    _uiEvent.send(UIEvent.GoToPayment)
                    UIState.TryPayment(
                        transactionType = operation,
                        isRefund = isRefund,
                        sessionID = sessionID
                    )
                }

                return@collect
            }
        }
}
