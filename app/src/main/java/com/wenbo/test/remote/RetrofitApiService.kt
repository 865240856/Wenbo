package com.wenbo.test.remote

import com.wenbo.test.model.CurrenciesResponse
import com.wenbo.test.model.ExchangeRatesResponse
import com.wenbo.test.model.WalletBalanceResponse
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitApiService(private val retrofit: Retrofit) : ApiService {
    private val service by lazy { retrofit.create(ApiService::class.java) }

    override suspend fun getSupportedCurrencies(): Response<CurrenciesResponse> {
        return service.getSupportedCurrencies()
    }

    override suspend fun getExchangeRates(): Response<ExchangeRatesResponse> {
        return service.getExchangeRates()
    }

    override suspend fun getWalletBalances(): Response<WalletBalanceResponse> {
        return service.getWalletBalances()
    }
}