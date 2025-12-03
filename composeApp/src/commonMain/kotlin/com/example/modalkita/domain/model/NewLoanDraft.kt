package com.example.modalkita.domain.model

data class NewLoanDraft(
    val amount: Long,
    val tenorMonths: Int,
    val purpose: LoanPurpose,
    val description: String,
    val supportingDocUrl: String? = null
)
