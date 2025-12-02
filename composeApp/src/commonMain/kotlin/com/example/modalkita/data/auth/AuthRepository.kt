package com.example.modalkita.data.auth

interface AuthRepository {

    suspend fun signUpBorrower(data: BorrowerRegisterData): Result<Unit>

    suspend fun signUpInvestor(data: InvestorRegisterData): Result<Unit>

    suspend fun login(email: String, password: String): Result<Unit>
}
