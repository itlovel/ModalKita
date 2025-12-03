package com.example.modalkita.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateMidtransLinkRequest(
    val loan_id: String,
    val amount: Long,
    val loan_name: String
)

@Serializable
data class CreateMidtransLinkResponse(
    val payment_link_url: String
)
