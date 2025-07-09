package com.wenbo.test.model

data class WalletBalanceResponse(
    val ok: Boolean,
    val warning: String,
    val wallet: List<WalletBalance>
)