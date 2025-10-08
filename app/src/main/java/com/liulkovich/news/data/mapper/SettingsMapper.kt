package com.liulkovich.news.data.mapper

import com.liulkovich.news.domain.entity.RefreshConfig
import com.liulkovich.news.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig {
    return RefreshConfig(language,interval,wifiOnly)
}