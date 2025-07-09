package com.wenbo.test.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wenbo.test.model.BalanceItem
import com.wenbo.test.model.Config
import com.wenbo.test.model.Currency
import com.wenbo.test.model.UserPreferences
import com.wenbo.test.remote.ApiServiceFactory
import com.wenbo.test.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val apiServiceFactory: ApiServiceFactory
) : ViewModel() {

    private val _balanceItems = MutableStateFlow(emptyList<BalanceItem>())
    val balanceItems: StateFlow<List<BalanceItem>> = _balanceItems

    private val _currencies = MutableStateFlow(emptyList<Currency>())
    val currencies: StateFlow<List<Currency>> = _currencies

    private val _userPrefs = MutableStateFlow<UserPreferences?>(null)
    val userPrefs: StateFlow<UserPreferences?> = _userPrefs

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        loadDashboardData()
    }

    private val _useMockApi = MutableStateFlow(Config.DEBUG)
    val useMockApi: StateFlow<Boolean> = _useMockApi

    fun toggleApiMode(useMock: Boolean) {
        _useMockApi.value = useMock
        // 重新创建 API 服务
        repository.updateApiService(apiServiceFactory.create(useMock))
        // 重新加载数据
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                val dashboardData = async { repository.getDashboardData() }
                val userPrefsData = async { repository.getUserPreferences() }
                val currenciesData = async { repository.getSupportedCurrencies() }

                _balanceItems.value = dashboardData.await()
                _userPrefs.value = userPrefsData.await()
                _currencies.value = currenciesData.await()

                applySorting()

                _loadingState.value = LoadingState.SUCCESS
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    fun toggleCurrencyEnabled(currencyId: String, enabled: Boolean) {
        viewModelScope.launch {
            val currentPrefs = _userPrefs.value ?: UserPreferences()
            val newEnabledSet = if (enabled) {
                currentPrefs.enabledCurrencies + currencyId
            } else {
                currentPrefs.enabledCurrencies - currencyId
            }

            repository.updateUserPreferences(newEnabledSet)
            _userPrefs.value = currentPrefs.copy(enabledCurrencies = newEnabledSet)
            loadDashboardData()
        }
    }

    fun updateSortOption(sortOption: UserPreferences.SortOption) {
        viewModelScope.launch {
            repository.updateSortOption(sortOption)
            _userPrefs.value = _userPrefs.value?.copy(sortOption = sortOption)
            applySorting()
        }
    }

    private fun applySorting() {
        val sortOption = _userPrefs.value?.sortOption ?: UserPreferences.SortOption.VALUE_DESC
        val sortedList = when (sortOption) {
            UserPreferences.SortOption.NAME -> _balanceItems.value.sortedBy { it.currency.name }
            UserPreferences.SortOption.VALUE_ASC -> _balanceItems.value.sortedBy { it.usdValue }
            UserPreferences.SortOption.VALUE_DESC -> _balanceItems.value.sortedByDescending { it.usdValue }
            UserPreferences.SortOption.BALANCE -> _balanceItems.value.sortedByDescending { it.balance }
        }
        _balanceItems.value = sortedList
    }
}

enum class LoadingState { IDLE, LOADING, SUCCESS, ERROR }