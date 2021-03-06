package com.niskender.newswithhmssearchkit.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.niskender.newswithhmssearchkit.data.NewsRepository
import com.niskender.newswithhmssearchkit.data.TokenRequestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(
        tokenRequestService: TokenRequestService
    ): NewsRepository {
        return NewsRepository(tokenRequestService)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }


    @Provides
    @Singleton
    fun providesRetrofitClientForTokenRequest(okHttpClient: OkHttpClient): TokenRequestService {
        val baseUrl = "https://oauth-login.cloud.huawei.com/"

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(TokenRequestService::class.java)
    }

}