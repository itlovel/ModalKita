package com.example.modalkita.domain.auth

import com.example.modalkita.core.AppResult

interface AuthRepository {

    suspend fun registerBorrower(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        businessName: String,
        city: String,
        sector: String,
        businessDuration: String
    ): AppResult<UserProfile>

    suspend fun registerInvestor(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): AppResult<UserProfile>

    suspend fun login(
        email: String,
        password: String
    ): AppResult<UserProfile>
}