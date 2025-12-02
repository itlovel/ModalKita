package com.example.modalkita.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data input dari form borrower (step 1 + step 2)
data class BorrowerRegisterData(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val businessName: String,
    val city: String,
    val sector: String,
    val businessDuration: String,
)

// Data input dari form investor
data class InvestorRegisterData(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
)

// Data baris di tabel user_profiles Supabase
@Serializable
data class UserProfile(
    @SerialName("id")
    val id: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    // "BORROWER" / "INVESTOR"
    @SerialName("role")
    val role: String,

    @SerialName("business_name")
    val businessName: String? = null,

    @SerialName("city")
    val city: String? = null,

    @SerialName("sector")
    val sector: String? = null,

    @SerialName("business_duration")
    val businessDuration: String? = null,
)

