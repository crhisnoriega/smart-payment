package br.com.trybu.payment.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.com.trybu.payment.api.Resources
import br.com.trybu.payment.api.safeAPICall
import br.com.trybu.payment.data.KeyRepository
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.db.TransactionDao
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction
import br.com.trybu.payment.db.entity.TransactionStatus
import br.com.trybu.payment.db.entity.currentDate
import br.com.trybu.payment.util.sanitizeToSend
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingViewModel @Inject
constructor(
    private val transactionDao: TransactionDao,
    private val paymentRepository: PaymentRepository,
    private val keyRepository: KeyRepository,
    private val plugPag: PlugPag,
) : ViewModel() {

    var pendingTransactions by mutableStateOf<List<Transaction>>(listOf())
    var _uiState by mutableStateOf<UIState>(UIState.Nothing)

    private var _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun checkPendingTransactions() = CoroutineScope(Dispatchers.IO).launch {
        val transactions = pendingTransactions()
        pendingTransactions = transactions

        tryToSend(transactions)
    }

    private fun pendingTransactions() = transactionDao.pendingTransaction()

    private suspend fun tryToSend(pendingTransactions: List<Transaction>) {

        pendingTransactions.forEach { transaction ->
            when (transaction.status) {
                Status.CREATED -> {
                    //doAbortPayment(transaction)
                    val lastTransaction = plugPag.getLastApprovedTransaction()

                    lastTransaction?.amount

                    val transactionCopy = transaction.copy(
                        jsonTransaction = Gson().toJson(lastTransaction),
                        transactionStatus = TransactionStatus.APPROVED
                    )
                    sendAndPersistTransaction(transactionCopy)
                }
                Status.PROCESSED -> sendAndPersistTransaction(transaction)
                Status.ERROR_ACK -> sendTransactionResult(transaction)
                else -> {}
            }
        }



        delay(1000)

        _uiState = if (pendingTransactions().isEmpty()) {
            UIState.SuccessPending
        } else {
            UIState.FailPending
        }
    }

    private suspend fun doAbortPayment(transaction: Transaction) {
        try {
            paymentRepository.abortPayment(
                transactionId = transaction.id,
                sessionID = transaction.sessionID,
                key = keyRepository.retrieveKey()
            ).catch {
                transactionDao.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_ACK))
            }.collect {
                if (it.body()?.errors?.isEmpty() == true) {
                    transactionDao.insertOrUpdateTransaction(transaction.copy(status = Status.ACK_SEND))
                } else {
                    transactionDao.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_ACK))
                }
            }
        } catch (e: Throwable) {
            transactionDao.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_ACK))
        }
    }

    private suspend fun sendAndPersistTransaction(
        transaction: Transaction
    ) {
        when (transaction.transactionStatus) {
            TransactionStatus.APPROVED, TransactionStatus.REJECTED -> {
                sendTransactionResult(transaction)
            }

            else -> {}
        }
    }

    private suspend fun sendTransactionResult(
        transaction: Transaction,
        isRefund: Boolean? = false
    ) {
        safeAPICall {
            if (isRefund == false) {
                paymentRepository.confirmPayment(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction.sanitizeToSend(),
                    key = keyRepository.retrieveKey() ?: "",
                    sessionID = transaction.sessionID
                )
            } else {
                paymentRepository.confirmRefund(
                    transactionId = transaction.id,
                    jsonTransaction = transaction.jsonTransaction.sanitizeToSend(),
                    key = keyRepository.retrieveKey() ?: ""
                )
            }
        }.collect {
            when (it) {
                is Resources.Success<*> -> updateTransactionAsStatus(transaction, Status.ACK_SEND)
                is Resources.Error -> updateTransactionAsStatus(transaction, Status.ERROR_ACK)
                else -> {}
            }
        }
    }

    private fun updateTransactionAsStatus(
        transaction: Transaction,
        status: Status
    ) {
        val transactionEntity = transaction.copy(
            jsonTransaction = transaction.jsonTransaction,
            lastUpdate = currentDate(),
            status = status
        )
        transactionDao.insertOrUpdateTransaction(transactionEntity)
    }
}