package com.example.modalkita.domain.usecase

import com.example.modalkita.domain.model.NewLoanDraft
import com.example.modalkita.domain.model.NewLoanSummary

class CalculateLoanSummaryUseCase {

    // misal: bunga flat 20% per tahun,
    // penalti flat Rp 10.000 / hari
    private val interestRate = 0.20
    private val penaltyPerDay = 10_000L

    operator fun invoke(draft: NewLoanDraft): NewLoanSummary {
        val interestAmount = (draft.amount * interestRate).toLong()
        val totalToPay = draft.amount + interestAmount
        val installment = totalToPay / draft.tenorMonths.coerceAtLeast(1)

        return NewLoanSummary(
            draft = draft,
            principal = draft.amount,
            interestAmount = interestAmount,
            totalToPay = totalToPay,
            installmentPerMonth = installment,
            penaltyPerDayLate = penaltyPerDay
        )
    }
}
