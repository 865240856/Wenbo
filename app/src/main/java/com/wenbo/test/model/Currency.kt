package com.wenbo.test.model

data class Currency(
    val coin_id: String,
    val name: String,
    val colorful_image_url: String,
    val token_decimal: Int,
    val is_active: Boolean = true,
    val priority: Int = 0
)