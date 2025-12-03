package com.example.modalkita.data.funding

import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToLong
//import kotlin.system.getTimeMillis

// ============= ENUM UNTUK SEKTOR, SKOR, TUJUAN =============

@Serializable
enum class CreditScore {
    PEMULA,
    BAIK,
    SANGAT_BAIK,
}

@Serializable
enum class BusinessSector {
    KULINER,
    FASHION,
    RETAIL,
    JASA,
    AGRIKULTUR,
    KECANTIKAN,
    KERAJINAN,
    OTOMOTIF,
    TEKNOLOGI,
    LAINNYA,
}

@Serializable
enum class LoanPurpose {
    MODAL_USAHA,
    LAINNYA,
}

// ============= DATA UMKM DI TABEL umkm_loans =============

@Serializable
data class UmkmFunding(
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("purpose")
    val purpose: LoanPurpose,

    @SerialName("credit_score")
    val creditScore: CreditScore,

    @SerialName("sector")
    val sector: BusinessSector,

    @SerialName("amount")
    val amount: Long,

    @SerialName("tenor_months")
    val tenorMonths: Int,

    @SerialName("funded_percentage")
    val fundedPercentage: Int,

    @SerialName("investors_count")
    val investorsCount: Int,

    // URL Payment Link Midtrans untuk UMKM ini
    @SerialName("payment_link_url")
    val paymentLinkUrl: String,
)

// ============= RIWAYAT INVESTASI DI TABEL investments =============

@Serializable
data class InvestmentHistory(
    val id: String,

    @SerialName("loan_id")
    val loanId: String,

    @SerialName("loan_name")
    val loanName: String,

    @SerialName("transaction_month_label")
    val monthLabel: String,          // contoh: "Jul 2025"

    @SerialName("transaction_date")
    val transactionDate: String,     // contoh ISO string, atau format text dari DB

    @SerialName("amount")
    val amount: Long,

    @SerialName("tx_hash")
    val txHash: String? = null,
)

// ============= BLOCKCHAIN SEDERHANA (POC) =============

@Serializable
data class Block(
    val index: Int,
    val timestampMillis: Long,
    val previousHash: String,
    val data: String,
    val hash: String,
)

class SimpleBlockchain {

    private val chain = mutableListOf<Block>()

    init {
        if (chain.isEmpty()) {
            chain += createGenesisBlock()
        }
    }

    private fun createGenesisBlock(): Block {
        val data = "GENESIS"
        val hash = calculateHash(0, 0L, "0", data)
        return Block(
            index = 0,
            timestampMillis = 0L,
            previousHash = "0",
            data = data,
            hash = hash
        )
    }

    private fun calculateHash(
        index: Int,
        timestamp: Long,
        previousHash: String,
        data: String
    ): String {
        val input = "$index$timestamp$previousHash$data"
        // Demo: hashCode saja (bukan kriptografi beneran)
        return input.hashCode().toString()
    }

    fun latestBlock(): Block = chain.last()

    fun addInvestmentBlock(investment: InvestmentHistory): Block {
        val previous = latestBlock()
        val newIndex = previous.index + 1
        val now = getTimeMillis()
        val data = "INVESTMENT:${investment.id}:${investment.loanId}:${investment.amount}"
        val hash = calculateHash(newIndex, now, previous.hash, data)
        val block = Block(
            index = newIndex,
            timestampMillis = now,
            previousHash = previous.hash,
            data = data,
            hash = hash
        )
        chain += block
        return block
    }

    fun allBlocks(): List<Block> = chain.toList()
}

// ============= "SMART CONTRACT" LOGIC SEDERHANA =============

object InvestmentContractRules {

    const val MIN_INVESTMENT: Long = 500_000L       // Rp 500.000
    const val MAX_INVESTMENT: Long = 500_000_000L   // Rp 500.000.000
    const val INTEREST_RATE = 0.10                  // 10%

    data class ContractResult(
        val isAllowed: Boolean,
        val reasonIfRejected: String? = null,
        val interest: Long = 0L,
        val monthlyReturn: Long = 0L,
    )

    fun validateAndCalculate(
        funding: UmkmFunding,
        amount: Long
    ): ContractResult {
        if (amount < MIN_INVESTMENT) {
            return ContractResult(
                isAllowed = false,
                reasonIfRejected = "Minimal investasi adalah Rp 500.000"
            )
        }
        if (amount > MAX_INVESTMENT) {
            return ContractResult(
                isAllowed = false,
                reasonIfRejected = "Maksimal investasi adalah Rp 500.000.000"
            )
        }

        val maxAllowedForThisLoan =
            ((100 - funding.fundedPercentage) / 100.0 * funding.amount).roundToLong()
        if (maxAllowedForThisLoan > 0 && amount > maxAllowedForThisLoan) {
            return ContractResult(
                isAllowed = false,
                reasonIfRejected = "Nominal melebihi sisa pendanaan yang tersedia"
            )
        }

        val interest = (amount * INTEREST_RATE).roundToLong()
        val monthlyReturn =
            if (funding.tenorMonths > 0) (amount + interest) / funding.tenorMonths
            else amount + interest

        return ContractResult(
            isAllowed = true,
            interest = interest,
            monthlyReturn = monthlyReturn
        )
    }
}

// ============= UTIL FORMAT RUPIAH =============

fun formatRupiah(amount: Long): String {
    val raw = amount.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in raw.length - 1 downTo 0) {
        sb.append(raw[i])
        count++
        if (count == 3 && i > 0) {
            sb.append('.')
            count = 0
        }
    }
    return "Rp. " + sb.reverse().toString()
}
