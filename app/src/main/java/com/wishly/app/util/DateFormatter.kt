package com.wishly.app.util

object DateFormatter {
    fun toLocalDateTime(dateString: String): String {
        if (dateString.contains("T")) return dateString

        val cleaned = dateString.trim()

        if (cleaned.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
            return "${cleaned}T00:00:00"
        }
        return cleaned
    }

    fun toDisplayDate(localDateTime: String?): String {
        return localDateTime?.substringBefore("T") ?: ""
    }
}