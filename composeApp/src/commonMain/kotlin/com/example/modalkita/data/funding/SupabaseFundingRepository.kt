package com.example.modalkita.data.funding

import com.example.modalkita.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

class SupabaseFundingRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : FundingRepository {

    override suspend fun getOpenFundings(searchQuery: String?): List<UmkmFunding> {
        val list = client.postgrest["umkm_loans"]
            .select()
            .decodeList<UmkmFunding>()

        return if (searchQuery.isNullOrBlank()) {
            list
        } else {
            list.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    override suspend fun getInvestmentHistory(): List<InvestmentHistory> {
        return client.postgrest["investments"]
            .select()
            .decodeList<InvestmentHistory>()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createInvestment(
        funding: UmkmFunding,
        amount: Long
    ): InvestmentHistory {

        // 1. Smart-contract: validasi nominal + hitung bunga & return
        val rules = InvestmentContractRules.validateAndCalculate(funding, amount)
        if (!rules.isAllowed) {
            throw IllegalArgumentException(rules.reasonIfRejected ?: "Investasi tidak valid")
        }

        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("Harus login sebagai investor")

        // 2. Build label tanggal
        val nowInstant = Instant.fromEpochMilliseconds(getTimeMillis())
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        val monthLabel = "${monthNameIndo(now.monthNumber)} ${now.year}"
        val dateLabel = "${now.dayOfMonth} ${monthNameIndo(now.monthNumber)} ${now.year}"

        // 3. Buat block blockchain POC
        val tempHistory = InvestmentHistory(
            id = "temp",
            loanId = funding.id,
            loanName = funding.name,
            monthLabel = monthLabel,
            transactionDate = dateLabel,
            amount = amount,
            txHash = null
        )

        val block = SimpleBlockchain().addInvestmentBlock(tempHistory)
        val txHash = block.hash

        // 4. Insert ke tabel investments dan decode jadi InvestmentHistory
        val insertDto = InvestmentInsertDto(
            investorId = user.id,
            loanId = funding.id,
            loanName = funding.name,
            amount = amount,
            transactionMonthLabel = monthLabel,
            transactionDate = dateLabel,
            txHash = txHash
        )

        return client.postgrest["investments"]
            .insert(insertDto) {
                select() // minta row yang baru diinsert
            }
            .decodeSingle<InvestmentHistory>()
    }

    private fun monthNameIndo(month: Int): String = when (month) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> month.toString()
    }
}

@Serializable
private data class InvestmentInsertDto(
    @SerialName("investor_id")
    val investorId: String,

    @SerialName("loan_id")
    val loanId: String,

    @SerialName("loan_name")
    val loanName: String,

    @SerialName("amount")
    val amount: Long,

    @SerialName("transaction_month_label")
    val transactionMonthLabel: String,

    @SerialName("transaction_date")
    val transactionDate: String,

    @SerialName("tx_hash")
    val txHash: String,
)
