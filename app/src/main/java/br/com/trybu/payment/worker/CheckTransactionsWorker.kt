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
import br.com.trybu.payment.di.RemoteModule
import javax.inject.Inject


class CheckTransactionsWorker @Inject constructor(
    private val appContext: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(appContext, workerParams) {


    private var smartPaymentAPI = RemoteModule.provideRetrofit().create(SmartPaymentAPI::class.java)

    override suspend fun doWork(): Result {
        try {
            Log.i("log", "repository: $smartPaymentAPI")
            val database = Room.databaseBuilder(
                appContext,
                TransactionDB::class.java, "database-name"
            ).build()

            val transaction = database.transactionDao().pendingTransaction(Status.PENDING).first()
            Log.i("log", "pending: ${transaction}")

            smartPaymentAPI.paymentConfirm(
                ConfirmRequest(
                    transactionId = transaction.id,
                    jsonRaw = transaction.jsonTransaction ?: "",
                    key = transaction.id
                )
            )
        } catch (e: Exception) {
            Log.i("log", e.message, e)
        }


        return Result.success()
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