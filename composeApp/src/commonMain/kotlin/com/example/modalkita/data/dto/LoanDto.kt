package com.example.modalkita.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoanDto(
    val id: String,
    @SerialName("borrower_id")
    val borrowerId: String,
    val amount: Long,
    val status: String,
    @SerialName("funded_amount")
    val fundedAmount: Long? = null,
    @SerialName("investor_count")
    val investorCount: Int? = null
)
