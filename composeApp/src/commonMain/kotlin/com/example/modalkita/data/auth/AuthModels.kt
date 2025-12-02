package com.example.modalkita.data.auth

data class BorrowerStep1Data(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
)

// State umum untuk AuthViewModel
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUser: UserProfile? = null,
)
