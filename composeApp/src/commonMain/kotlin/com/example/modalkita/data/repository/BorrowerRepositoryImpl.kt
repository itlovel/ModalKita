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
import com.example.modalkita.data.remote.dto.CreateMidtransLinkRequest
import com.example.modalkita.data.remote.dto.CreateMidtransLinkResponse


// Ktor client untuk call Edge Function Midtrans
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

import com.example.modalkita.data.funding.LoanApplicationBlockchainPayload
import com.example.modalkita.data.funding.SimpleBlockchain
import io.ktor.http.isSuccess

/* -------------------------------------------------------------------------- */
/*                         INTERNAL DASHBOARD MAPPING                         */
/* -------------------------------------------------------------------------- */

private data class LoanDashboardInternal(
    val status: LoanApplicationStatus,
    val fundedPercentage: Int,
    val investorCount: Int,
    val fundedAmount: Long?,
    val totalAmount: Long?
)

// Untuk sementara: TIDAK memakai kolom status dari LoanDto
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

    // sementara: tidak baca loan.status dari DB
    val status = LoanApplicationStatus.NONE

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

/* -------------------------------------------------------------------------- */
/*                     EDGE FUNCTION REQUEST/RESPONSE DTO                     */
/* -------------------------------------------------------------------------- */

// HttpClient sederhana untuk panggil Edge Function
private val httpClient = HttpClient(CIO)

private suspend fun callCreateMidtransLinkEdgeFunction(
    supabaseClient: SupabaseClient,
    loanId: String
): String {
    val functionUrl =
        "https://mhbhaesdeqnzpzccqmle.supabase.co/functions/v1/create_midtrans_link"

    val accessToken = supabaseClient.auth.currentAccessTokenOrNull()

    val response = httpClient.post(functionUrl) {
        contentType(ContentType.Application.Json)
        setBody(
            buildJsonObject {
                put("loan_id", loanId)
            }
        )
        if (accessToken != null) {
            header("Authorization", "Bearer $accessToken")
        }
    }

    if (!response.status.isSuccess()) {
        throw IllegalStateException("Failed to call create_midtrans_link: ${response.status}")
    }

    val body = response.body<CreateMidtransLinkResponse>()
    if (body.payment_link_url.isBlank()) {
        throw IllegalStateException("Empty payment_link_url from edge function")
    }

    return body.payment_link_url
}



/* -------------------------------------------------------------------------- */
/*                           BORROWER REPOSITORY IMPL                         */
/* -------------------------------------------------------------------------- */

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

        // 1) Siapkan nama pinjaman
        val loanName = draft.description.ifBlank { "Pengajuan Pinjaman UMKM" }

        // 2) Payload blockchain
        val payload = LoanApplicationBlockchainPayload(
            borrowerId = user.id,
            loanName = loanName,
            amount = draft.amount,
            tenorMonths = draft.tenorMonths,
            purpose = draft.purpose.name
        )

        // 3) Tambah blok baru ke SimpleBlockchain & ambil hash
        val block = SimpleBlockchain().addLoanApplicationBlock(payload)
        val txHash = block.hash

        // 4) Insert loan ke tabel umkm_loans
        val body = buildJsonObject {
            put("borrower_id", user.id)
            put("name", loanName)
            put("purpose", draft.purpose.name)
            put("credit_score", "BAIK")
            put("sector", "LAINNYA")
            put("amount", draft.amount)
            put("tenor_months", draft.tenorMonths)
            put("funded_percentage", 0)
            put("investors_count", 0)

            draft.supportingDocUrl?.let { url ->
                put("supporting_doc_url", url)
            }

            put("tx_hash", txHash)
        }

        val insertResult = supabaseClient
            .from("umkm_loans")
            .insert(body) {
                select()
            }

        val insertedLoans = insertResult.decodeList<LoanDto>()
        val newLoan = insertedLoans.firstOrNull()
            ?: error("Gagal membaca loan yang baru dibuat dari Supabase")

        val loanId = newLoan.id

        // 5) Panggil Edge Function Midtrans — dengan error handling
        try {
            val paymentLink = callCreateMidtransLinkEdgeFunction(
                supabaseClient = supabaseClient,
                loanId = loanId,      // sekarang cukup loanId
            )

            // Optional: kalau mau, kamu bisa pakai paymentLink di UI, tapi DB sudah diupdate oleh Edge Function.
            // Misal: return / emit result ke ViewModel.

        } catch (e: Exception) {
            // Di sini kamu bisa:
            // - log error
            // - optional: lempar lagi biar UI bisa nunjukin "Gagal membuat link pembayaran"
            throw IllegalStateException("Gagal membuat link pembayaran Midtrans", e)
        }
    }

}
