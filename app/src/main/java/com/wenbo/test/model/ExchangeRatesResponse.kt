package com.wenbo.test.model

data class ExchangeRatesResponse(
    val ok: Boolean,
    val warning: String,
    val tiers: List<ExchangeRate>
)