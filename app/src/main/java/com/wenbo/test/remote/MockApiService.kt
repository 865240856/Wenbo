package com.wenbo.test.remote

import com.google.gson.Gson
import com.wenbo.test.model.CurrenciesResponse
import com.wenbo.test.model.ExchangeRatesResponse
import com.wenbo.test.model.MockDataSource
import com.wenbo.test.model.WalletBalanceResponse
import retrofit2.Response

class MockApiService : ApiService {
    private val gson = Gson()

    override suspend fun getSupportedCurrencies(): Response<CurrenciesResponse> {
        return Response.success(
            gson.fromJson(MockDataSource.currenciesJson, CurrenciesResponse::class.java)
        )
    }

    override suspend fun getExchangeRates(): Response<ExchangeRatesResponse> {
        return Response.success(
            gson.fromJson(MockDataSource.exchangeRatesJson, ExchangeRatesResponse::class.java)
        )
    }

    override suspend fun getWalletBalances(): Response<WalletBalanceResponse> {
        return Response.success(
            gson.fromJson(MockDataSource.walletBalanceJson, WalletBalanceResponse::class.java)
        )
    }
}