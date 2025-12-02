package com.example.modalkita.domain.model

data class BorrowerHomeDashboard(
    val username: String,
    val creditScore: Int,
    val creditCategory: CreditCategory,
    val canApply: Boolean,
    val activeApplicationStatus: LoanApplicationStatus,
    val fundedPercentage: Int,
    val investorCount: Int,
    val loanAmount: Long?,
    val fundedAmount: Long?
)
