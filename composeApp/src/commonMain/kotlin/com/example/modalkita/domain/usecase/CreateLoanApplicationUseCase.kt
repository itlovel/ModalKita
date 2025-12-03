package com.example.modalkita.domain.usecase

import com.example.modalkita.domain.model.NewLoanDraft
import com.example.modalkita.domain.model.NewLoanSummary
import com.example.modalkita.domain.repository.BorrowerRepository

class CreateLoanApplicationUseCase(
    private val borrowerRepository: BorrowerRepository
) {
    suspend operator fun invoke(draft: NewLoanDraft, summary: NewLoanSummary) {
        borrowerRepository.createLoanApplication(draft, summary)
    }
}
