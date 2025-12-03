package com.example.modalkita.domain.repository

import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.model.NewLoanDraft
import com.example.modalkita.domain.model.NewLoanSummary

interface BorrowerRepository {
    suspend fun getBorrowerHomeDashboard(): BorrowerHomeDashboard
    suspend fun createLoanApplication(draft: NewLoanDraft, summary: NewLoanSummary)
}
