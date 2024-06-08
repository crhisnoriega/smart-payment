package br.com.trybu.payment.db

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.trybu.payment.db.entity.Status
import br.com.trybu.payment.db.entity.Transaction

@Dao
abstract class TransactionDao {

    @Query("select * from `transaction` where id == :id")
    abstract fun findTransaction(id: String): Transaction?

    @Insert
    abstract fun insertTransaction(transaction: Transaction): Long

    @Update
    abstract fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM `transaction`")
    abstract fun getTransactions(): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE  status != :status")
    abstract fun pendingTransaction(status: Status): List<Transaction>

    fun insertOrUpdateTransaction(transaction: Transaction) {
        findTransaction(transaction.id)?.let {
            updateTransaction(transaction)
            Log.i("log", "updated")
        } ?: run {
            val result = insertTransaction(transaction)
            Log.i("log", "result: $result id: ${transaction.id}")
        }
    }
}