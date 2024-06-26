package br.com.trybu.payment.worker

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.data.model.ConfirmRequest
import br.com.trybu.payment.db.TransactionDB
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction
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

            val sharedPreference =
                appContext.getSharedPreferences("smart_payment", Context.MODE_PRIVATE)
            val key = sharedPreference.getString("key", "") ?: throw Exception("dont key found")

            val pendingTransactions = transactionDAO.pendingTransaction()

            Log.i("log", "was found: ${pendingTransactions.size} pending transactions")

            pendingTransactions.forEach { transaction ->
                try {
                    val result = sendTransaction(transaction, key)
                    transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ACK_SEND))
                } catch (e: Throwable) {
                    transactionDAO.insertOrUpdateTransaction(transaction.copy(status = Status.ERROR_ACK))
                }
            }


        } catch (e: Throwable) {
            Log.i("log", e.message, e)
        }


        return Result.success()
    }


    private suspend fun sendTransaction(transaction: Transaction, key: String) =
        smartPaymentAPI.paymentConfirm(
            ConfirmRequest(
                transactionId = transaction.id,
                jsonRaw = transaction.jsonTransaction ?: "",
                key = key
            )
        )


    companion object {
        fun startWorker(context: Context) = run {
            val request = OneTimeWorkRequestBuilder<CheckTransactionsWorker>().build()
            WorkManager
                .getInstance(context)
                .enqueue(request)
        }
    }

}