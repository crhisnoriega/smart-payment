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
    val status: Status
)

enum class Status {
    CREATED,
    INITIALIZED,
    PENDING,
    APPROVED,
    REJECTED,
    CANCELED,
    SENT_UPDATED,
    SENT_ERROR, LOCAL_ERROR
}

fun currentDate() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())