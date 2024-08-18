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
class InitializationnViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,
    private val transactionDao: TransactionDao,
) : ViewModel() {

    private var _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var _uiState by mutableStateOf<UIState>(UIState.Nothing)


    fun retrieveKey() = CoroutineScope(Dispatchers.IO).launch {
        paymentRepository.retrieveKey(serialNumber = Build.SERIAL)
            .collect { establishment ->
                if (establishment?.errors?.isEmpty() == true) {
                    establishment.key.let { keyRepository.persisKey(it) }

                    val pendingTransactions = transactionDao.pendingTransaction()

                    if (pendingTransactions.isEmpty()) {
                        _uiState = UIState.InitializeSuccess(
                            establishmentName = establishment.establismentName,
                            establishmentDocument = establishment.document
                        )
                        _uiEvent.send(UIEvent.GoToInformation)
                    } else {
                        _uiEvent.send(UIEvent.GoToPending)
                    }
                } else {
                    _uiState = UIState.InitializeFail
                    _uiEvent.send(UIEvent.GoToInformation)
                }
            }
    }

    fun exit() {

    }
}
