package br.com.trybu.payment.di


import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import br.com.trybu.payment.api.SmartPaymentAPI
import br.com.trybu.payment.db.TransactionDB
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {


    @Singleton
    @Provides
    fun providePaymentApi(
        retrofit: Retrofit
    ): SmartPaymentAPI {
        return retrofit.create(SmartPaymentAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideLocalStorage(@ApplicationContext context: Context) =
        context.getSharedPreferences("smart_payment", Context.MODE_PRIVATE)


    @Singleton
    @Provides
    fun providePagSeguroPlug(@ApplicationContext context: Context) = PlugPag(context)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().create()

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY


        val builder = OkHttpClient.Builder()
        builder.connectTimeout(60, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(logInterceptor)

        val httpClient = builder.build()
        return Retrofit.Builder()
            .baseUrl("https://svc2.elosgate.com.br/generated/gatewaysvc.svc/json/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient).build()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        TransactionDB::class.java, "database-name"
    ).build()

    @Provides
    fun provideTransactionDAO(transactionDB: TransactionDB) = transactionDB.transactionDao()
}