package br.com.trybu.payment.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.trybu.payment.db.entity.Transaction

@Dao
abstract class TransactionDao {

    @Query("select * from `transaction` where id == :id")
    abstract fun findTransaction(id: String): Transaction?

    @Insert
    abstract fun insertTransaction(transaction: Transaction)

    @Update
    abstract fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM `transaction`")
    abstract fun getTransactions(): List<Transaction>

    fun insertOrUpdateTransaction(transaction: Transaction) {
        findTransaction(transaction.id)?.let {
            updateTransaction(transaction)
        }
    }
}