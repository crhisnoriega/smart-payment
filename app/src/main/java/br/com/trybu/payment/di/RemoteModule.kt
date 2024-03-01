package br.com.trybu.payment.di


import br.com.trybu.payment.api.SmartPaymentAPI
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().create()
        val httpClient = OkHttpClient()
        return Retrofit.Builder()
            .baseUrl("https://svc-hom.elosgate.com.br/generated/gatewaysvc.svc/json")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient).build()
    }
}