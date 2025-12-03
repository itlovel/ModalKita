package com.example.modalkita.data.repository

import com.example.modalkita.data.dto.BorrowerDto
import com.example.modalkita.data.dto.LoanDto
import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.model.CreditCategory
import com.example.modalkita.domain.model.LoanApplicationStatus
import com.example.modalkita.domain.model.NewLoanDraft
import com.example.modalkita.domain.model.NewLoanSummary
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

        // ==== 1. Ambil data borrower (boleh kosong) ====
        val borrowerList = supabaseClient
            .from("borrowers")
            .select {
                filter {
                    eq("id", user.id)
                }
            }
            .decodeList<BorrowerDto>()

        val borrowerRow = borrowerList.firstOrNull()

        // ==== 2. Tentukan username & creditScore default ====
        val metaUsername = (user.userMetadata?.get("username") as? String)

        val username: String =
            borrowerRow?.username
                ?: metaUsername
                ?: user.email?.substringBefore("@")
                ?: "User"

        val creditScore: Int = borrowerRow?.creditScore ?: 1000  // DEFAULT 1000

        // ==== 3. Ambil 1 loan aktif (boleh null) ====
        val loanRow = supabaseClient
            .from("umkm_loans")
            .select {
                filter {
                    eq("borrower_id", user.id)
                    // kalau mau exclude yg sudah selesai:
                    // neq("status", "completed")
                }
            }
            .decodeList<LoanDto>()
            .firstOrNull()

        val loanDashboard = mapLoanToDashboard(loanRow)
        val category: CreditCategory = creditScore.toCreditCategory()

        return BorrowerHomeDashboard(
            username = username,
            creditScore = creditScore,
            creditCategory = category,
            canApply = category.canApply(),
            activeApplicationStatus = loanDashboard.status,
            fundedPercentage = loanDashboard.fundedPercentage,
            investorCount = loanDashboard.investorCount,
            loanAmount = loanDashboard.totalAmount,
            fundedAmount = loanDashboard.fundedAmount
        )
    }

    override suspend fun createLoanApplication(
        draft: NewLoanDraft,
        summary: NewLoanSummary
    ) {
        val user = supabaseClient.auth.currentUserOrNull()
            ?: error("User not logged in")

        supabaseClient
            .from("umkm_loans")
            .insert(
                mapOf(
                    "borrower_id" to user.id,
                    "amount" to draft.amount,
                    "tenor_months" to draft.tenorMonths,
                    "purpose" to draft.purpose.label,
                    "description" to draft.description,
                    "status" to "peninjauan",  // default
                    // opsional simpan field yg lain:
                    "total_to_pay" to summary.totalToPay,
                    "installment_per_month" to summary.installmentPerMonth,
                    "penalty_per_day" to summary.penaltyPerDayLate
                )
            )
    }

}
