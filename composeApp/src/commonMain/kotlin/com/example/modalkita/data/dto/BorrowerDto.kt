package com.example.modalkita.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BorrowerDto(
    val id: String,
    val username: String,
    @SerialName("credit_score")
    val creditScore: Int
)
