package com.example.modalkita.domain.usecase

import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.repository.BorrowerRepository

class GetBorrowerDashboardUseCase(
    private val borrowerRepository: BorrowerRepository
) {
    suspend operator fun invoke(): BorrowerHomeDashboard {
        return borrowerRepository.getBorrowerHomeDashboard()
    }
}
