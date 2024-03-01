package br.com.trybu.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.data.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    fun retrieveOperations() = viewModelScope.launch {

    }

    fun retrieveKey() = viewModelScope.launch {
        paymentRepository.retrieveKey(serialNumber = "PBA1238673598")
            .catch {
                Log.i("log", it.message, it)
            }.collect {
                Log.i("log", "key: $it")
            }
    }
}