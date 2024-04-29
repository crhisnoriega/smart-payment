package br.com.trybu.payment.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.trybu.payment.db.entity.Transaction

@Database(
    entities = [Transaction::class],
    version = 1
)
abstract class TransactionDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

}