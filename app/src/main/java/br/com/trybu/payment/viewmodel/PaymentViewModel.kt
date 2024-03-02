package br.com.trybu.payment.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.data.model.RetrieveOperationsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository
) : ViewModel() {

    var operations by mutableStateOf<List<RetrieveOperationsResponse.Items.Operation>>(listOf())

    fun retrieveOperations(document: String) = viewModelScope.launch {
        paymentRepository.retrieveOperations(keyRepository.retrieveKey(), document)
            .catch {

            }.collect {
                operations = it ?: listOf()
            }
    }

    fun retrieveKey() = viewModelScope.launch {
        paymentRepository.retrieveKey(serialNumber = "PBA1238673598")
            .catch {
            }.collect { key ->
                Log.i("log", "key: $key")
                key?.let { keyRepository.persisKey(it) }
            }
    }
}