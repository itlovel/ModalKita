package com.example.modalkita.data.funding

interface FundingRepository {

    suspend fun getOpenFundings(
        searchQuery: String? = null
    ): List<UmkmFunding>

    suspend fun getInvestmentHistory(): List<InvestmentHistory>

    suspend fun createInvestment(
        funding: UmkmFunding,
        amount: Long
    ): InvestmentHistory
}
