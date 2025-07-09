package com.wenbo.test.repository

import com.wenbo.test.model.Currency
import com.wenbo.test.model.UserPreferences

interface LocalCache {
    suspend fun saveCurrencies(currencies: List<Currency>)
    suspend fun getCurrencies(): List<Currency>?

    suspend fun saveUserPreferences(prefs: UserPreferences)
    suspend fun getUserPreferences(): UserPreferences
}
