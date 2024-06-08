package br.com.trybu.payment.worker

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.data.PaymentRepository
import br.com.trybu.payment.data.model.ConfirmRequest
import br.com.trybu.payment.db.TransactionDB
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction
import br.com.trybu.payment.db.entity.TransactionStatus
import br.com.trybu.payment.di.RemoteModule
import javax.inject.Inject


class CheckTransactionsWorker @Inject constructor(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private var smartPaymentAPI = RemoteModule.provideRetrofit().create(SmartPaymentAPI::class.java)

    override suspend fun doWork(): Result {
        try {
            val transactionDAO = Room.databaseBuilder(
                appContext,
                TransactionDB::class.java, "database-name"
            ).build().transactionDao()

            val pendingTransactions = transactionDAO.pendingTransaction(
                status = Status.PENDING_SEND,
                transactionStatus = TransactionStatus.APPROVED
            )

            Log.i("log", "was found: ${pendingTransactions.size} pending transactions")

            pendingTransactions.forEach { transaction ->
                try {
                    sendTransaction(transaction)
                    transactionDAO.insertTransaction(transaction.copy(status = Status.ACK_SEND))
                } catch (e: Throwable) {
                    transactionDAO.insertTransaction(transaction.copy(status = Status.PENDING_SEND))
                }
            }


        } catch (e: Throwable) {
            Log.i("log", e.message, e)
        }


        return Result.success()
    }


    private suspend fun sendTransaction(transaction: Transaction) {
        smartPaymentAPI.paymentConfirm(
            ConfirmRequest(
                transactionId = transaction.id,
                jsonRaw = transaction.jsonTransaction ?: "",
                key = transaction.id
            )
        )
    }

    companion object {
        fun startWorker(context: Context) = run {
            val request = OneTimeWorkRequestBuilder<CheckTransactionsWorker>().build()
            WorkManager
                .getInstance(context)
                .enqueue(request)
        }
    }

}