package com.example.modalkita.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.modalkita.data.auth.AuthRepository
import com.example.modalkita.data.auth.BorrowerRegisterData
import com.example.modalkita.data.auth.InvestorRegisterData
import com.example.modalkita.data.auth.SupabaseAuthRepository

class AuthViewModel(
    private val repository: AuthRepository = SupabaseAuthRepository()
) {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun resetMessages() {
        errorMessage = null
        successMessage = null
    }

    suspend fun registerBorrower(data: BorrowerRegisterData) {
        isLoading = true
        resetMessages()

        val result = repository.signUpBorrower(data)
        isLoading = false

        result.fold(
            onSuccess = {
                successMessage = "Registrasi borrower berhasil."
            },
            onFailure = { e ->
                errorMessage = e.message ?: "Terjadi kesalahan saat registrasi borrower."
            }
        )
    }

    suspend fun registerInvestor(data: InvestorRegisterData) {
        isLoading = true
        resetMessages()

        val result = repository.signUpInvestor(data)
        isLoading = false

        result.fold(
            onSuccess = {
                successMessage = "Registrasi investor berhasil."
            },
            onFailure = { e ->
                errorMessage = e.message ?: "Terjadi kesalahan saat registrasi investor."
            }
        )
    }

    suspend fun login(email: String, password: String) {
        isLoading = true
        resetMessages()

        val result = repository.login(email, password)
        isLoading = false

        result.fold(
            onSuccess = {
                successMessage = "Login berhasil."
            },
            onFailure = { e ->
                errorMessage = e.message ?: "Email atau password salah."
            }
        )
    }
}
