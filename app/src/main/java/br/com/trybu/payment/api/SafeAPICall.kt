package br.com.trybu.payment.api


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response

suspend inline fun <T> safeAPICall(
    crossinline block: suspend () -> Flow<T>,
): Flow<Resources> = flow {
    emit(Resources.Loading)
    block.invoke()
        .flowOn(Dispatchers.IO)
        .catch { throwable ->
            emit(Resources.Error(throwable, throwable.message))
        }.collect {
            Log.i("log", "response: $it")
            (it as? Response<*>)?.let { response ->
                if (response.code() >= 400) emit(Resources.Error(Exception(), ""))
            } ?: kotlin.run {
                emit(Resources.Success(it))
            }
        }
}

sealed class Resources {
    object Loading : Resources()
    data class Success<T>(val data: T) : Resources()
    data class Error(val error: Throwable, val message: String?) : Resources()
}