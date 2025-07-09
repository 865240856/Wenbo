package com.wenbo.test.model

data class UserPreferences(
    val enabledCurrencies: Set<String> = setOf("BTC", "ETH", "CRO"),
    val sortOption: SortOption = SortOption.VALUE_DESC
) {
    enum class SortOption {
        NAME, VALUE_ASC, VALUE_DESC, BALANCE
    }
}