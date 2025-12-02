package com.example.modalkita.domain.model

enum class CreditCategory {
    SANGAT_BAIK,
    BAIK,
    KURANG
}

fun Int.toCreditCategory(): CreditCategory = when {
    this in 700..1000 -> CreditCategory.SANGAT_BAIK
    this in 400..699  -> CreditCategory.BAIK
    else              -> CreditCategory.KURANG
}

fun CreditCategory.canApply(): Boolean = this != CreditCategory.KURANG
