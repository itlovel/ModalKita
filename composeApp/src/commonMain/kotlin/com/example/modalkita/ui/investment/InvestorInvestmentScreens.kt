package com.example.modalkita.ui.investment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.data.funding.*
import com.example.modalkita.ui.components.ModalKitaChildTopBar
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
//import kotlin.system.getTimeMillis

// Ganti dengan Payment Link Midtrans Sandbox untuk PENARIKAN DANA
private const val MIDTRANS_WITHDRAW_PAYMENT_LINK_URL =
    "https://app.sandbox.midtrans.com/payment-links/YOUR-WITHDRAW-LINK-ID"

/* ============================================================
   ===================== ROOT NAV INVESTASI ===================
   ============================================================ */

/**
 * Root untuk tab "Investasi" (portofolio investor).
 * - Menampilkan daftar investasi per UMKM.
 * - Jika user klik salah satu, masuk ke detail jadwal investasi.
 */
@Composable
fun InvestorInvestmentRoot(
    modifier: Modifier = Modifier,
    fundingRepository: FundingRepository = SupabaseFundingRepository(),
    onOpenPaymentLink: (String) -> Unit
) {
    var selected by remember {
        mutableStateOf<SelectedInvestment?>(null)
    }

    val selectedInvestment = selected
    if (selectedInvestment != null) {
        // Layar DETAIL investasi
        InvestorInvestmentDetailScreen(
            investment = selectedInvestment.investment,
            funding = selectedInvestment.loan,
            onBack = { selected = null },
            onOpenPaymentLink = onOpenPaymentLink,
            modifier = modifier
        )
    } else {
        // Layar LIST investasi
        InvestorInvestmentListScreen(
            repository = fundingRepository,
            onInvestmentClicked = { inv, loan ->
                selected = SelectedInvestment(investment = inv, loan = loan)
            },
            modifier = modifier
        )
    }
}

private data class SelectedInvestment(
    val investment: InvestmentHistory,
    val loan: UmkmFunding
)

/* ============================================================
   ===================== LIST INVESTASI =======================
   ============================================================ */

private data class InvestorPortfolioItem(
    val investment: InvestmentHistory,
    val loan: UmkmFunding,
    val totalReceived: Long,   // investasi + bunga 10%
    val interest: Long,
    val monthlyReturn: Long
)

@Composable
private fun InvestorInvestmentListScreen(
    repository: FundingRepository,
    onInvestmentClicked: (InvestmentHistory, UmkmFunding) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<InvestorPortfolioItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // Ambil semua investasi milik investor yang login
            val history = repository.getInvestmentHistory()
            // Ambil semua UMKM (skalanya kecil untuk tugas, boleh fetch semua)
            val loans = repository.getOpenFundings(null)
            val loanById = loans.associateBy { it.id }

            val portfolioItems = history.mapNotNull { inv ->
                val loan = loanById[inv.loanId] ?: return@mapNotNull null
                val amount = inv.amount
                val interest = (amount * 0.10).roundToLong()
                val total = amount + interest
                val monthly =
                    if (loan.tenorMonths > 0) (total / loan.tenorMonths) else total

                InvestorPortfolioItem(
                    investment = inv,
                    loan = loan,
                    totalReceived = total,
                    interest = interest,
                    monthlyReturn = monthly
                )
            }

            items = portfolioItems
            if (portfolioItems.isEmpty()) {
                error = "Belum ada investasi yang tercatat."
            }
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat data investasi."
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ModalKitaColors.Green600)
        }
        return
    }

    if (error != null && items.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = error!!,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    val totalInvestasi = items.sumOf { it.totalReceived }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Investasi",
            style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = ModalKitaColors.Black500
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Mulai investasi ke UMKM pilihanmu.",
            style = typography.bodyMedium,
            color = ModalKitaColors.Black300
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card TOTAL INVESTASI (background hijau, rounded)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ModalKitaColors.Green600
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = "Total Investasi",
                    style = typography.bodyMedium,
                    color = ModalKitaColors.White50
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRupiah(totalInvestasi),
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = ModalKitaColors.White50
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Termasuk bunga yang didapatkan",
                    style = typography.bodySmall,
                    color = ModalKitaColors.White50
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { item ->
            InvestorInvestmentSummaryCard(
                item = item,
                onClick = { onInvestmentClicked(item.investment, item.loan) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InvestorInvestmentSummaryCard(
    item: InvestorPortfolioItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.loan.name,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = ModalKitaColors.Black500
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Penerimaan:",
                style = typography.bodySmall,
                color = ModalKitaColors.Black300
            )
            Text(
                text = formatRupiah(item.totalReceived),
                style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = ModalKitaColors.Green600
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(
                label = "Investasi",
                value = formatRupiah(item.investment.amount)
            )
            DetailRow(
                label = "Bunga (10%)",
                value = formatRupiah(item.interest)
            )
            DetailRow(
                label = "Return Bulanan",
                value = "+ ${formatRupiah(item.monthlyReturn)}"
            )
        }
    }
}

/* ============================================================
   ===================== DETAIL INVESTASI =====================
   ============================================================ */

private data class InvestmentScheduleRow(
    val index: Int,
    val monthLabel: String,
    val status: String,
    val dueDateLabel: String,
    val returnAmount: Long,
    val penaltyAmount: Long,
    val isPaidInitial: Boolean
)

@Composable
private fun InvestorInvestmentDetailScreen(
    investment: InvestmentHistory,
    funding: UmkmFunding,
    onBack: () -> Unit,
    onOpenPaymentLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    // Bangun jadwal dari data investasi + tenor
    val baseSchedule = remember(investment.id, funding.id) {
        buildScheduleRows(investment, funding)
    }

    // State apakah tiap bulan sudah ditarik atau belum (awal ikut dari isPaidInitial)
    val paidStates = remember(investment.id, funding.id) {
        mutableStateListOf<Boolean>().apply {
            baseSchedule.forEach { add(it.isPaidInitial) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        ModalKitaChildTopBar(
            title = "Detail Investasi",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Card nama UMKM besar (seperti gambar hijau di atas)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ModalKitaColors.Green600),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = funding.name,
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ModalKitaColors.White50,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Informasi Investasi UMKM
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informasi Investasi UMKM",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ModalKitaColors.Black500
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("ID Investasi", investment.id)
                    DetailRow("Total Pinjaman", formatRupiah(funding.amount))
                    DetailRow("Tenor", "${funding.tenorMonths} bulan")
                    DetailRow("Kredit Skor", funding.creditScore.toReadable())
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ModalKita: Denda/Penalti flat sebesar Rp. 10.000/hari dari jatuh tempo.",
                style = typography.bodySmall,
                color = ModalKitaColors.Black300
            )

            Spacer(modifier = Modifier.height(12.dp))

            // LIST CARD PER BULAN
            baseSchedule.forEachIndexed { idx, row ->
                val isPaid = paidStates.getOrNull(idx) ?: row.isPaidInitial

                InvestmentScheduleCard(
                    row = row,
                    isPaid = isPaid,
                    onWithdrawClicked = {
                        // Buka Payment Link Midtrans untuk simulasi penarikan
                        onOpenPaymentLink(MIDTRANS_WITHDRAW_PAYMENT_LINK_URL)
                        // Setelah "penarikan", tandai sebagai sudah diterima
                        if (idx in paidStates.indices) {
                            paidStates[idx] = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InvestmentScheduleCard(
    row: InvestmentScheduleRow,
    isPaid: Boolean,
    onWithdrawClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    // Jika sudah diterima → hijau, else putih
    val containerColor =
        if (isPaid) ModalKitaColors.Green600 else ModalKitaColors.White50
    val textColorPrimary =
        if (isPaid) ModalKitaColors.White50 else ModalKitaColors.Black500
    val textColorSecondary =
        if (isPaid) ModalKitaColors.White50 else ModalKitaColors.Black300

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: Bulan + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = row.monthLabel,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textColorPrimary
                )
                Text(
                    text = if (isPaid) "Telah Diterima" else "Belum Diterima",
                    style = typography.bodySmall,
                    color = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            DetailRow(
                label = "Tanggal",
                value = row.dueDateLabel,
                labelColor = textColorSecondary,
                valueColor = textColorPrimary
            )
            DetailRow(
                label = "Return",
                value = "+ ${formatRupiah(row.returnAmount)}",
                labelColor = textColorSecondary,
                valueColor = textColorPrimary
            )
            DetailRow(
                label = "Kompensasi",
                value = "Rp. ${row.penaltyAmount}",
                labelColor = textColorSecondary,
                valueColor = textColorPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PrimaryButton(
                    label = "Penarikan Dana",
                    onClick = onWithdrawClicked,
                    enabled = !isPaid,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                PrimaryButton(
                    label = "Download Invoice",
                    onClick = {
                        // TODO: arahkan ke file invoice (PDF) dari Supabase Storage
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )
            }
        }
    }
}

/* ============================================================
   ================== HELPER & EXTENSIONS =====================
   ============================================================ */

@Composable
private fun DetailRow(
    label: String,
    value: String,
    labelColor: androidx.compose.ui.graphics.Color = ModalKitaColors.Black300,
    valueColor: androidx.compose.ui.graphics.Color = ModalKitaColors.Black500
) {
    val typography = modalKitaTypography()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = typography.bodySmall,
            color = labelColor
        )
        Text(
            text = value,
            style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

/**
 * Membangun jadwal investasi per bulan berdasarkan:
 * - monthLabel investasi awal (misal "Juli 2025")
 * - tenor dari UMKM
 * - return bulanan = (investasi + bunga 10%) / tenor
 * - penalti flat 10.000/hari jika lewat jatuh tempo dan belum dibayar.
 *
 * Untuk demo tugas:
 * - Bulan pertama ditandai "Telah Diterima" secara default.
 * - Bulan ke-2 dst "Belum Diterima".
 */
@OptIn(ExperimentalTime::class)
private fun buildScheduleRows(
    investment: InvestmentHistory,
    funding: UmkmFunding
): List<InvestmentScheduleRow> {
    val amount = investment.amount
    val interest = (amount * 0.10).roundToLong()
    val total = amount + interest
    val monthly =
        if (funding.tenorMonths > 0) (total / funding.tenorMonths) else total

    // Ambil bulan & tahun mulai dari label bulan investasi awal ("Juli 2025")
    val startMonthYear = parseMonthLabel(investment.monthLabel)
    val (startMonth, startYear) = startMonthYear
        ?: run {
            // fallback: pakai bulan & tahun hari ini
            val now = Instant
                .fromEpochMilliseconds(getTimeMillis())
                .toLocalDateTime(TimeZone.currentSystemDefault())
            now.monthNumber to now.year
        }

    // Tanggal hari ini, untuk menghitung keterlambatan
    val today = Instant
        .fromEpochMilliseconds(getTimeMillis())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val rows = mutableListOf<InvestmentScheduleRow>()

    for (i in 0 until funding.tenorMonths) {
        val offset = startMonth - 1 + i
        val month = (offset % 12) + 1
        val year = startYear + (offset / 12)

        val monthLabel = "${monthNameIndo(month)} $year"
        val monthStr2 = month.toString().padStart(2, '0')
        val dueDateLabel = "10/$monthStr2/$year"
        val dueDate = LocalDate(year, month, 10)

        // Untuk demo: bulan pertama dianggap sudah diterima
        val isPaidInitial = (i == 0)

        // Kalau belum dibayar dan sudah lewat jatuh tempo, hitung denda 10.000/hari
        val daysLate =
            if (!isPaidInitial && today > dueDate) dueDate.daysUntil(today) else 0
        val penalty = daysLate * 10_000L

        rows += InvestmentScheduleRow(
            index = i,
            monthLabel = monthLabel,
            status = if (isPaidInitial) "Telah Diterima" else "Belum Diterima",
            dueDateLabel = dueDateLabel,
            returnAmount = monthly,
            penaltyAmount = penalty,
            isPaidInitial = isPaidInitial
        )
    }

    return rows
}

private fun parseMonthLabel(label: String): Pair<Int, Int>? {
    // contoh label: "Juli 2025"
    val parts = label.trim().split(" ")
    if (parts.size != 2) return null
    val month = when (parts[0].lowercase()) {
        "januari" -> 1
        "februari" -> 2
        "maret" -> 3
        "april" -> 4
        "mei" -> 5
        "juni" -> 6
        "juli" -> 7
        "agustus" -> 8
        "september" -> 9
        "oktober" -> 10
        "november" -> 11
        "desember" -> 12
        else -> return null
    }
    val year = parts[1].toIntOrNull() ?: return null
    return month to year
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

/**
 * Label human-readable untuk skor kredit (re-use dari funding).
 */
private fun CreditScore.toReadable(): String = when (this) {
    CreditScore.PEMULA -> "Pemula"
    CreditScore.BAIK -> "Baik"
    CreditScore.SANGAT_BAIK -> "Sangat Baik"
}
