package com.example.modalkita.data.repository

import com.example.modalkita.data.dto.BorrowerDto
import com.example.modalkita.data.dto.LoanDto
import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.model.CreditCategory
import com.example.modalkita.domain.model.LoanApplicationStatus
import com.example.modalkita.domain.model.canApply
import com.example.modalkita.domain.model.toCreditCategory
import com.example.modalkita.domain.repository.BorrowerRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

private data class LoanDashboardInternal(
    val status: LoanApplicationStatus,
    val fundedPercentage: Int,
    val investorCount: Int,
    val fundedAmount: Long?,
    val totalAmount: Long?
)

private fun mapLoanToDashboard(loan: LoanDto?): LoanDashboardInternal {
    if (loan == null) {
        return LoanDashboardInternal(
            status = LoanApplicationStatus.NONE,
            fundedPercentage = 0,
            investorCount = 0,
            fundedAmount = null,
            totalAmount = null
        )
    }

    val status = when (loan.status.lowercase()) {
        "pending", "peninjauan" -> LoanApplicationStatus.PENINJAUAN
        "funding", "pendanaan"  -> LoanApplicationStatus.PENDANAAN
        "ready_to_disburse"     -> LoanApplicationStatus.SIAP_DICAIRKAN
        else                    -> LoanApplicationStatus.NONE
    }

    val total = loan.amount
    val funded = loan.fundedAmount ?: 0L
    val percent = if (total > 0) ((funded * 100) / total).toInt() else 0
    val investors = loan.investorCount ?: 0

    return LoanDashboardInternal(
        status = status,
        fundedPercentage = percent,
        investorCount = investors,
        fundedAmount = funded,
        totalAmount = total
    )
}

class BorrowerRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : BorrowerRepository {

    override suspend fun getBorrowerHomeDashboard(): BorrowerHomeDashboard {
        val user = supabaseClient.auth.currentUserOrNull()
            ?: error("User not logged in")

        // borrowers row
        val borrowerRow = supabaseClient
            .from("borrowers")
            .select {
                filter {
                    eq("id", user.id)
                }
            }
            .decodeSingle<BorrowerDto>()

        // 1 loan aktif (kalau nanti mau filter status, tambahkan di filter)
        val loanRow = supabaseClient
            .from("umkm_loans")
            .select {
                filter {
                    eq("borrower_id", user.id)
                    // contoh kalau mau exclude yang sudah selesai:
                    // neq("status", "completed")
                }
            }
            .decodeList<LoanDto>()
            .firstOrNull()

        val loanDashboard = mapLoanToDashboard(loanRow)
        val category: CreditCategory = borrowerRow.creditScore.toCreditCategory()

        return BorrowerHomeDashboard(
            username = borrowerRow.username,
            creditScore = borrowerRow.creditScore,
            creditCategory = category,
            canApply = category.canApply(),
            activeApplicationStatus = loanDashboard.status,
            fundedPercentage = loanDashboard.fundedPercentage,
            investorCount = loanDashboard.investorCount,
            loanAmount = loanDashboard.totalAmount,
            fundedAmount = loanDashboard.fundedAmount
        )
    }
}
