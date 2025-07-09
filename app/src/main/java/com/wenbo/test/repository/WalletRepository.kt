package com.wenbo.test.repository

import com.wenbo.test.model.BalanceItem
import com.wenbo.test.model.Currency
import com.wenbo.test.model.UserPreferences
import com.wenbo.test.remote.ApiService
import javax.inject.Inject

class WalletRepository @Inject constructor(
    private val api: ApiService,
    private val cache: LocalCache
) {
    private var apiService: ApiService = api

    fun updateApiService(newApiService: ApiService) {
        this.apiService = newApiService
    }

    suspend fun getSupportedCurrencies(): List<Currency> {
        return cache.getCurrencies() ?: run {
            val response = api.getSupportedCurrencies()
            val currencies = response.body()?.currencies ?: emptyList()
            cache.saveCurrencies(currencies)
            currencies
        }
    }

    suspend fun getUserEnabledCurrencies(): List<Currency> {
        val allCurrencies = getSupportedCurrencies()
        val userPreferences = cache.getUserPreferences()

        return allCurrencies.filter { currency ->
            userPreferences.enabledCurrencies.contains(currency.coin_id)
        }.sortedBy { it.priority }
    }

    suspend fun getExchangeRates(currencyIds: List<String>): Map<String, Double> {
        val response = api.getExchangeRates()
        val allRates = response.body()?.tiers ?: emptyList()
        return allRates.filter { it.from_currency in currencyIds }
            .associate { it.from_currency to it.getUsdRate() }
    }

    suspend fun getWalletBalances(currencyIds: List<String>): Map<String, Double> {
        val response = api.getWalletBalances()
        val allBalances = response.body()?.wallet ?: emptyList()
        return allBalances.filter { it.currency in currencyIds }
            .associate { it.currency to it.amount }
    }

    suspend fun getDashboardData(): List<BalanceItem> {
        val enabledCurrencies = getUserEnabledCurrencies()
        val currencyIds = enabledCurrencies.map { it.coin_id }

        val rates = getExchangeRates(currencyIds)
        val balances = getWalletBalances(currencyIds)

        return enabledCurrencies.map { currency ->
            val balance = balances[currency.coin_id] ?: 0.0
            val rate = rates[currency.coin_id] ?: 0.0
            val usdValue = balance * rate

            BalanceItem(
                currency = currency,
                balance = balance,
                usdValue = usdValue,
                usdRate = rate
            )
        }
    }

    suspend fun updateUserPreferences(enabledCurrencies: Set<String>) {
        val currentPrefs = cache.getUserPreferences()
        val newPrefs = currentPrefs.copy(enabledCurrencies = enabledCurrencies)
        cache.saveUserPreferences(newPrefs)
    }


    // 添加缺失的方法
    suspend fun getUserPreferences(): UserPreferences {
        return cache.getUserPreferences()
    }

    // 添加货币切换方法
    suspend fun toggleCurrencyEnabled(currencyId: String, enabled: Boolean) {
        val currentPrefs = getUserPreferences()
        val newEnabledSet = if (enabled) {
            currentPrefs.enabledCurrencies + currencyId
        } else {
            currentPrefs.enabledCurrencies - currencyId
        }

        val newPrefs = currentPrefs.copy(enabledCurrencies = newEnabledSet)
        cache.saveUserPreferences(newPrefs)
    }

    // 添加排序方法
    suspend fun updateSortOption(sortOption: UserPreferences.SortOption) {
        val currentPrefs = getUserPreferences()
        val newPrefs = currentPrefs.copy(sortOption = sortOption)
        cache.saveUserPreferences(newPrefs)
    }

    // 添加获取所有货币的方法
    suspend fun getAllCurrencies(): List<Currency> {
        return cache.getCurrencies() ?: run {
            val response = api.getSupportedCurrencies()
            val currencies = response.body()?.currencies ?: emptyList()
            cache.saveCurrencies(currencies)
            currencies
        }
    }
}