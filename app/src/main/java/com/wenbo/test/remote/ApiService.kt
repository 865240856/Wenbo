package com.wenbo.test.remote

import com.wenbo.test.model.CurrenciesResponse
import com.wenbo.test.model.Currency
import com.wenbo.test.model.ExchangeRate
import com.wenbo.test.model.ExchangeRatesResponse
import com.wenbo.test.model.WalletBalance
import com.wenbo.test.model.WalletBalanceResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("currencies.json")
    suspend fun getSupportedCurrencies(): Response<CurrenciesResponse>

    @GET("exchange_rates.json")
    suspend fun getExchangeRates(): Response<ExchangeRatesResponse>

    @GET("wallet_balance.json")
    suspend fun getWalletBalances(): Response<WalletBalanceResponse>
}