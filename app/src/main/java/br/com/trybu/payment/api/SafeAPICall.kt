package br.com.trybu.payment.api


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

suspend inline fun <T> safeAPICall(
    crossinline block: suspend () -> Flow<T>,
): Flow<Resources> = flow {
    emit(Resources.Loading)
    block.invoke()
        .flowOn(Dispatchers.IO)
        .catch { throwable ->
            when (throwable) {
                is HttpException -> {
                    when (throwable.code()) {
                        400, 401 -> emit(Resources.Error(throwable, throwable.message))
                    }
                }

                else -> {
                    emit(Resources.Error(throwable, throwable.message))
                }
            }

        }.collect {
            emit(Resources.Success(it))
        }
}

sealed class Resources {
    object Loading : Resources()
    data class Success<T>(val data: T) : Resources()
    data class Error(val error: Throwable, val message: String?) : Resources()
}