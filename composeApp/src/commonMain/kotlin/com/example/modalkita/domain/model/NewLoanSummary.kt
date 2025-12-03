package com.example.modalkita.domain.model

data class NewLoanSummary(
    val draft: NewLoanDraft,
    val principal: Long,
    val interestAmount: Long,
    val totalToPay: Long,
    val installmentPerMonth: Long,
    val penaltyPerDayLate: Long
)
