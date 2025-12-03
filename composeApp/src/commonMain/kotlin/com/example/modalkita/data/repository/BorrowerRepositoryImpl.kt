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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


import com.example.modalkita.data.funding.LoanApplicationBlockchainPayload
import com.example.modalkita.data.funding.SimpleBlockchain


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

        // 1) Siapkan nama pinjaman (sementara pakai deskripsi sebagai judul)
        val loanName = draft.description.ifBlank { "Pengajuan Pinjaman UMKM" }

        // 2) Siapkan payload untuk blockchain
        val payload = LoanApplicationBlockchainPayload(
            borrowerId = user.id,
            loanName = loanName,
            amount = draft.amount,
            tenorMonths = draft.tenorMonths,
            // Bisa pilih: simpan enum name ("MODAL_USAHA") atau label ("Modal Usaha")
            purpose = draft.purpose.name
        )

        // 3) Tambahkan blok baru ke SimpleBlockchain & ambil hash-nya
        val block = SimpleBlockchain().addLoanApplicationBlock(payload)
        val txHash = block.hash

        // 4) Build JSON body untuk umkm_loans (bukan Map<String, Any?> lagi)
        val body = buildJsonObject {
            put("borrower_id", user.id)

            // field wajib di schema kamu:
            put("name", loanName)
            put("purpose", draft.purpose.name)   // disimpan sebagai text
            put("credit_score", "BAIK")          // sementara default
            put("sector", "LAINNYA")             // sementara default

            put("amount", draft.amount)
            put("tenor_months", draft.tenorMonths)

            // opsional: kalau kolom ini ada di schema
            put("funded_percentage", 0)
            put("investors_count", 0)

            // field tambahan
            draft.supportingDocUrl?.let { url ->
                put("supporting_doc_url", url)
            }

            put("tx_hash", txHash)
        }

        // 5) Insert ke tabel umkm_loans
        supabaseClient
            .from("umkm_loans")
            .insert(body)
    }



}
