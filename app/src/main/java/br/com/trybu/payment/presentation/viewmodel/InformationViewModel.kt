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
class InformationViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val plugPag: PlugPag
) : ViewModel() {

    var reprint = mutableStateOf(false)
    var _uiState by mutableStateOf<UIState>(UIState.Nothing)
    var qrCode = MutableLiveData<String>()


    fun printLast() = CoroutineScope(Dispatchers.IO).launch {
        reprint.value = true
        plugPag.reprintCustomerReceipt()
        reprint.value = false
    }


    fun updateUIState(uiState: UIState?) {
        if (uiState != null && _uiState !is UIState.HideInformation) {
            _uiState = uiState
            Handler(Looper.getMainLooper()).postDelayed({
                _uiState = UIState.HideInformation
            }, 2000)
        }
    }


    fun openCamera() {

    }

    fun hideInfo() {

    }

}
