package com.example.modalkita.ui.funding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.modalkita.data.funding.*

class FundingViewModel(
    private val repository: FundingRepository = SupabaseFundingRepository(),
) {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var fundings by mutableStateOf<List<UmkmFunding>>(emptyList())
        private set

    var histories by mutableStateOf<List<InvestmentHistory>>(emptyList())
        private set

    suspend fun loadFundings(searchQuery: String = "") {
        isLoading = true
        errorMessage = null
        try {
            fundings = repository.getOpenFundings(searchQuery)
        } catch (e: Throwable) {
            errorMessage = e.message ?: "Gagal memuat data pendanaan"
        } finally {
            isLoading = false
        }
    }

    suspend fun loadHistories() {
        isLoading = true
        errorMessage = null
        try {
            histories = repository.getInvestmentHistory()
        } catch (e: Throwable) {
            errorMessage = e.message ?: "Gagal memuat riwayat investasi"
        } finally {
            isLoading = false
        }
    }

    suspend fun invest(
        funding: UmkmFunding,
        amount: Long
    ): InvestmentHistory? {
        errorMessage = null
        return try {
            val result = repository.createInvestment(funding, amount)
            histories = histories + result
            result
        } catch (e: Throwable) {
            errorMessage = e.message ?: "Gagal melakukan investasi"
            null
        }
    }
}
