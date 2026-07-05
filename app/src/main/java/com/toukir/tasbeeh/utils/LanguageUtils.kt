package com.toukir.tasbeeh.utils

import java.util.Locale

fun formatNumber(number: Int, language: String): String {
    return if (language == "bn") {
        number.toString().map {
            when (it) {
                '0' -> '০'
                '1' -> '১'
                '2' -> '২'
                '3' -> '৩'
                '4' -> '৪'
                '5' -> '৫'
                '6' -> '৬'
                '7' -> '৭'
                '8' -> '৮'
                '9' -> '৯'
                else -> it
            }
        }.joinToString("")
    } else {
        number.toString()
    }
}

fun getLocalizedNumber(number: Int): String {
    val currentLocale = Locale.getDefault()
    return if (currentLocale.language == "bn") {
        formatNumber(number, "bn")
    } else {
        number.toString()
    }
}
