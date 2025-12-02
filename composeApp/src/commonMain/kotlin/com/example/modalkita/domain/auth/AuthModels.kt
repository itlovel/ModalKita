package com.example.modalkita.domain.auth

enum class UserRole {
    BORROWER,
    INVESTOR
}

data class UserProfile(
    val id: String,        // auth.users.id
    val fullName: String,
    val phone: String?,
    val role: UserRole,
    val businessName: String? = null,
    val city: String? = null,
    val sector: String? = null,
    val businessDuration: String? = null
)