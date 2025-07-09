package com.wenbo.test.di

import android.content.Context
import com.wenbo.test.model.Config
import com.wenbo.test.remote.ApiService
import com.wenbo.test.remote.ApiServiceFactory
import com.wenbo.test.remote.MockApiService
import com.wenbo.test.repository.DataStoreCache
import com.wenbo.test.repository.LocalCache
import com.wenbo.test.repository.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://real.api.base.url/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiServiceFactory(
        retrofit: Retrofit
    ): ApiServiceFactory {
        return ApiServiceFactory(retrofit)
    }

    @Provides
    @Singleton
    fun provideApiService(factory: ApiServiceFactory): ApiService {
        // 默认在 DEBUG 构建时使用 Mock，RELEASE 使用真实 API
        return factory.create(useMock = Config.DEBUG)
    }

    @Provides
    @Singleton
    fun provideLocalCache(@ApplicationContext context: Context): LocalCache {
        return DataStoreCache(context)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(api: ApiService, cache: LocalCache): WalletRepository {
        return WalletRepository(api, cache)
    }
}