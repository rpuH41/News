package com.liulkovich.news.domain.entity

data class RefreshConfig(
    val language: Language,
    val interval: Interval,
    val wifiOnly: Boolean
)