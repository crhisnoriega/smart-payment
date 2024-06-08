package br.com.trybu.payment.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date


@Entity
data class Transaction(
    @PrimaryKey val id: String,
    val jsonTransaction: String? = null,
    val createDate: String,
    val lastUpdate: String,
    val status: Status,
    val transactionStatus: TransactionStatus,
    val transactionType: TransactionType
)

enum class Status {
    CREATED,
    PROCESSED,
    ERROR_SEND,
    ACK_SEND
}


enum class TransactionStatus {
    NONE,
    APPROVED,
    REJECTED
}

enum class TransactionType {
    NONE,
    PAYMENT,
    REFUND
}



fun currentDate() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())