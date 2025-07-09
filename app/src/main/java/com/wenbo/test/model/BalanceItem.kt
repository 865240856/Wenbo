package com.wenbo.test.model

data class BalanceItem(
    val currency: Currency,
    val balance: Double,
    val usdValue: Double,
    val usdRate: Double
)