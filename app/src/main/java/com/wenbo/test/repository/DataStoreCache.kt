package com.wenbo.test.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.wenbo.test.model.Currency
import com.wenbo.test.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.createDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_prefs"
)
class DataStoreCache @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalCache {

    private val dataStore = context.createDataStore

    private object PreferencesKeys {
        val ENABLED_CURRENCIES = stringSetPreferencesKey("enabled_currencies")
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val CURRENCIES_JSON = stringPreferencesKey("currencies_json")
    }

    override suspend fun saveCurrencies(currencies: List<Currency>) {
        val json = Gson().toJson(currencies)
        dataStore.edit { settings ->
            settings[PreferencesKeys.CURRENCIES_JSON] = json
        }
    }

    override suspend fun getCurrencies(): List<Currency>? {
        val json = dataStore.data.map { it[PreferencesKeys.CURRENCIES_JSON] }.firstOrNull()
        return if (json != null) {
            Gson().fromJson(json, Array<Currency>::class.java).toList()
        } else {
            null
        }
    }

    override suspend fun saveUserPreferences(prefs: UserPreferences) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.ENABLED_CURRENCIES] = prefs.enabledCurrencies
            settings[PreferencesKeys.SORT_OPTION] = prefs.sortOption.name
        }
    }

    override suspend fun getUserPreferences(): UserPreferences {
        return dataStore.data.map { settings ->
            val enabledSet = settings[PreferencesKeys.ENABLED_CURRENCIES] ?: (getCurrencies()?.map { it.coin_id }?.toSet() ?: setOf())
            val sortOption = settings[PreferencesKeys.SORT_OPTION]?.let {
                UserPreferences.SortOption.valueOf(it)
            } ?: UserPreferences.SortOption.VALUE_DESC

            UserPreferences(
                enabledCurrencies = enabledSet,
                sortOption = sortOption
            )
        }.first()
    }
}