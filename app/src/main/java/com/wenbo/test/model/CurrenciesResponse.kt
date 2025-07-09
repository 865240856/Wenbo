package com.wenbo.test.model

data class CurrenciesResponse(
    val currencies: List<Currency>,
    val total: Int,
    val ok: Boolean
)