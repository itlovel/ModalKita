package com.example.modalkita.domain.repository

import com.example.modalkita.domain.model.BorrowerHomeDashboard

interface BorrowerRepository {
    suspend fun getBorrowerHomeDashboard(): BorrowerHomeDashboard
}
