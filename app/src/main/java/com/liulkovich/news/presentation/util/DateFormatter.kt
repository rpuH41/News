package com.liulkovich.news.presentation.util

import java.text.DateFormat
import java.text.SimpleDateFormat

private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

fun Long.formatDate(): String {
    return formatter.format(this)
}