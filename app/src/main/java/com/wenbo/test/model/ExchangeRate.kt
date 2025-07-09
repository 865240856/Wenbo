package com.wenbo.test.model

data class ExchangeRate(
    val from_currency: String,
    val rates: List<Rate>
) {
    fun getUsdRate(): Double {
        return rates.firstOrNull()?.rate?.toDoubleOrNull() ?: 0.0
    }
}
data class Rate(
    val amount: String,
    val rate: String
)