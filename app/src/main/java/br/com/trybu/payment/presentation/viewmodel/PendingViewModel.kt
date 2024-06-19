package br.com.trybu.payment.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.db.TransactionDB
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingViewModel @Inject
constructor(
    private val transactionDB: TransactionDB,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,
) : ViewModel() {


    var pendingTransactions by mutableStateOf<List<Transaction>>(listOf())
    var statePending by mutableStateOf("")

    fun checkPendingTransactions() = CoroutineScope(Dispatchers.IO).launch {
        val transactions = pendingTransactions()
        pendingTransactions = transactions

        tryToSend(transactions)
    }

    private fun pendingTransactions() = transactionDB.transactionDao().pendingTransaction(
        status = arrayOf(Status.PROCESSED, Status.ERROR_SEND)
    )

    suspend fun tryToSend(pendingTransactions: List<Transaction>) {
        val transactionDAO = transactionDB.transactionDao()
        pendingTransactions.forEach { transaction ->
            try {
                paymentRepository.abortPayment(
                    transactionId = transaction.id,
                    sessionID = transaction.sessionID,
                    key = keyRepository.retrieveKey()
                ).catch {
                    transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_SEND))
                }.collect {
                    if (it.body()?.errors?.isEmpty() == true) {
                        transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ACK_SEND))
                    } else {
                        transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_SEND))
                    }
                }

            } catch (e: Throwable) {
                transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_SEND))
            }
        }

        statePending = if (pendingTransactions().isEmpty()) {
            "success"
        } else {
            "fail"
        }

    }
}