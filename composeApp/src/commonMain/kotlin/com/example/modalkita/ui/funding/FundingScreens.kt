package com.example.modalkita.ui.funding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.data.funding.*
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import kotlinx.coroutines.launch

private enum class FundingTab {
    DISCOVER,
    HISTORY
}

/**
 * Root untuk tab "Pendanaan" di bottom nav investor.
 * Hanya boleh diakses setelah login sebagai INVESTOR.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestorFundingRoot(
    modifier: Modifier = Modifier,
    fundingRepository: FundingRepository = SupabaseFundingRepository(),
    onOpenPaymentLink: (String) -> Unit
) {
    val typography = modalKitaTypography()
    var selectedTab by remember { mutableStateOf(FundingTab.DISCOVER) }
    var selectedFunding by remember { mutableStateOf<UmkmFunding?>(null) }

    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }

    // ==== MODE DETAIL (1 UMKM) ====
    val detailFunding = selectedFunding
    if (detailFunding != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Detail Pendanaan",
                            style = typography.titleLarge,
                            color = ModalKitaColors.Black500
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedFunding = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            UmkmDetailScreen(
                funding = detailFunding,
                isSubmitting = isSubmitting,
                submitError = submitError,
                onBack = { selectedFunding = null },
                onInvestConfirmed = { amount ->
                    scope.launch {
                        try {
                            isSubmitting = true
                            submitError = null

                            // 1. Catat investasi + tx_hash + blockchain POC
                            val history = fundingRepository.createInvestment(
                                funding = detailFunding,
                                amount = amount
                            )

                            // 2. Ambil Payment Link dari data UMKM
                            val paymentUrl = detailFunding.paymentLinkUrl
                            if (paymentUrl.isNullOrBlank()) {
                                submitError =
                                    "Link pembayaran Midtrans belum dikonfigurasi untuk UMKM ini."
                            } else {
                                // 3. Buka Payment Link Midtrans di browser
                                onOpenPaymentLink(paymentUrl)

                                // 4. (Opsional) kembali ke list
                                selectedFunding = null
                            }

                        } catch (e: Exception) {
                            submitError = e.message ?: "Terjadi kesalahan saat mencatat investasi"
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
        return
    }

    // ==== MODE LIST + RIWAYAT ====
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Pendanaan UMKM",
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Temukan UMKM, salurkan pendanaan, dapatkan return bulanan.",
                    style = typography.bodyMedium,
                    color = ModalKitaColors.Black300
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Slider tab: Investasi / Riwayat
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(ModalKitaColors.White50),
                ) {
                    FundingTabChip(
                        label = "Investasi",
                        selected = selectedTab == FundingTab.DISCOVER,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = FundingTab.DISCOVER }
                    )
                    FundingTabChip(
                        label = "Riwayat",
                        selected = selectedTab == FundingTab.HISTORY,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = FundingTab.HISTORY }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            FundingTab.DISCOVER -> InvestorFundingListScreen(
                repository = fundingRepository,
                onUmkmSelected = { selectedFunding = it },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )

            FundingTab.HISTORY -> InvestmentHistoryScreen(
                repository = fundingRepository,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun FundingTabChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val typography = modalKitaTypography()
    Surface(
        modifier = modifier
            .padding(2.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) ModalKitaColors.Green600 else ModalKitaColors.White50
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) ModalKitaColors.White50 else ModalKitaColors.Black300
            )
        }
    }
}

/* ============================================================
   ================== LIST INVESTASI (DISCOVER) ===============
   ============================================================ */

@Composable
private fun InvestorFundingListScreen(
    repository: FundingRepository,
    onUmkmSelected: (UmkmFunding) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var allFundings by remember { mutableStateOf<List<UmkmFunding>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            allFundings = repository.getOpenFundings(null)
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat data UMKM"
        } finally {
            isLoading = false
        }
    }

    val filtered = remember(allFundings, searchQuery) {
        if (searchQuery.isBlank()) {
            allFundings
        } else {
            allFundings.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text("Cari UMKM berdasarkan nama", style = typography.bodyMedium)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ModalKitaColors.Green600,
                unfocusedBorderColor = ModalKitaColors.Green600,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ModalKitaColors.Green600)
            }
            return@Column
        }

        if (error != null) {
            Text(
                text = error!!,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        if (filtered.isEmpty()) {
            Text(
                text = "Belum ada UMKM yang dapat didanai.",
                style = typography.bodyMedium,
                color = ModalKitaColors.Black300
            )
            return@Column
        }

        filtered.forEach { funding ->
            UmkmFundingCard(
                funding = funding,
                onClick = { onUmkmSelected(funding) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UmkmFundingCard(
    funding: UmkmFunding,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModalKitaColors.White50
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = funding.name,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = ModalKitaColors.Black500
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${funding.sector.toReadable()} • ${funding.purpose.toReadable()}",
                style = typography.bodySmall,
                color = ModalKitaColors.Black300
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Skor Kredit: ${funding.creditScore.toReadable()}",
                style = typography.bodySmall,
                color = ModalKitaColors.Black500
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Jumlah Pinjaman: ${formatRupiah(funding.amount)}",
                style = typography.bodySmall,
                color = ModalKitaColors.Black500
            )

            Text(
                text = "Tenor: ${funding.tenorMonths} bulan",
                style = typography.bodySmall,
                color = ModalKitaColors.Black500
            )

            Spacer(modifier = Modifier.height(12.dp))

            val progress = (funding.fundedPercentage.coerceIn(0, 100)) / 100f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = ModalKitaColors.Green600,
                trackColor = ModalKitaColors.White50
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${funding.fundedPercentage}% dana terkumpul • ${funding.investorsCount} investor",
                style = typography.bodySmall,
                color = ModalKitaColors.Black300
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PrimaryButton(
                    label = "Investasi",
                    onClick = onClick,
                    modifier = Modifier
                        .height(40.dp)
                        .widthIn(min = 140.dp)
                )
            }
        }
    }
}

/* ============================================================
   ================== DETAIL UMKM (INVESTASI) =================
   ============================================================ */

@Composable
private fun UmkmDetailScreen(
    funding: UmkmFunding,
    isSubmitting: Boolean,
    submitError: String?,
    onBack: () -> Unit,
    onInvestConfirmed: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var nominalText by remember { mutableStateOf("") }
    var contractError by remember { mutableStateOf<String?>(null) }
    var interest by remember { mutableStateOf<Long?>(null) }
    var monthlyReturn by remember { mutableStateOf<Long?>(null) }
    var agreed by remember { mutableStateOf(false) }

    fun recompute() {
        val amount = nominalText.filter { it.isDigit() }.toLongOrNull()
        if (amount == null) {
            contractError = null
            interest = null
            monthlyReturn = null
            return
        }
        val result = InvestmentContractRules.validateAndCalculate(funding, amount)
        if (!result.isAllowed) {
            contractError = result.reasonIfRejected
            interest = null
            monthlyReturn = null
        } else {
            contractError = null
            interest = result.interest
            monthlyReturn = result.monthlyReturn
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Info UMKM
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = funding.name,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${funding.sector.toReadable()} • ${funding.purpose.toReadable()}",
                    style = typography.bodySmall,
                    color = ModalKitaColors.Black300
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Detail pinjaman
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Detail Pengajuan",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailRow("Jumlah Pengajuan", formatRupiah(funding.amount))
                DetailRow("Tenor", "${funding.tenorMonths} bulan")
                DetailRow("Skor Kredit", funding.creditScore.toReadable())
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input nominal
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nominal Investasi",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Minimal Rp 500.000 dan maksimal Rp 500.000.000.",
                    style = typography.bodySmall,
                    color = ModalKitaColors.Black300
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nominalText,
                    onValueChange = {
                        nominalText = it.filter { ch -> ch.isDigit() }
                        recompute()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("Rp ", style = typography.bodyLarge) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ModalKitaColors.Green600,
                        unfocusedBorderColor = ModalKitaColors.Green600,
                    )
                )

                if (contractError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = contractError!!,
                        style = typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ringkasan keuntungan
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ringkasan Keuntungan",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ModalKitaColors.Black500
                )
                Spacer(modifier = Modifier.height(8.dp))

                val amountLong = nominalText.toLongOrNull()

                DetailRow("Nominal Investasi", amountLong?.let { formatRupiah(it) } ?: "-")
                DetailRow(
                    "Estimasi Bunga (10%)",
                    interest?.let { formatRupiah(it) } ?: "-"
                )
                DetailRow(
                    "Estimasi Pencairan Bulanan",
                    monthlyReturn?.let { formatRupiah(it) } ?: "-"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox persetujuan
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = { agreed = it },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Saya memahami risiko dan menyetujui ketentuan investasi pada aplikasi ModalKita.",
                style = typography.bodySmall,
                color = ModalKitaColors.Black300
            )
        }

        if (submitError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = submitError,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val amountLong = nominalText.toLongOrNull()
        val canSubmit = amountLong != null &&
                contractError == null &&
                agreed &&
                !isSubmitting

        PrimaryButton(
            label = if (isSubmitting) "Memproses..." else "Investasi Sekarang",
            enabled = canSubmit,
            onClick = {
                val amount = amountLong ?: return@PrimaryButton
                onInvestConfirmed(amount)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    val typography = modalKitaTypography()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = typography.bodySmall,
            color = ModalKitaColors.Black300
        )
        Text(
            text = value,
            style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = ModalKitaColors.Black500,
            textAlign = TextAlign.End
        )
    }
}

/* ============================================================
   ================== RIWAYAT INVESTASI =======================
   ============================================================ */

@Composable
private fun InvestmentHistoryScreen(
    repository: FundingRepository,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    var history by remember { mutableStateOf<List<InvestmentHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            history = repository.getInvestmentHistory()
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat riwayat investasi"
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

    if (error != null) {
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

    if (history.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada riwayat investasi.",
                style = typography.bodyMedium,
                color = ModalKitaColors.Black300
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        items(history) { item ->
            InvestmentHistoryCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun InvestmentHistoryCard(
    history: InvestmentHistory
) {
    val typography = modalKitaTypography()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ModalKitaColors.White50),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = history.monthLabel,
                style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = ModalKitaColors.Black300
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = history.loanName,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = ModalKitaColors.Black500
            )

            Spacer(modifier = Modifier.height(4.dp))

            DetailRow("Tanggal Transaksi", history.transactionDate)
            DetailRow("Nominal Investasi", formatRupiah(history.amount))

            if (!history.txHash.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tx Hash: ${history.txHash}",
                    style = typography.bodySmall,
                    color = ModalKitaColors.Black300
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button Download Invoice -> untuk tugas cukup placeholder
            PrimaryButton(
                label = "Download Invoice",
                onClick = {
                    // TODO: bisa diarahkan ke file PDF dari Supabase / storage
                },
                modifier = Modifier
                    .height(40.dp)
                    .widthIn(min = 160.dp)
            )
        }
    }
}

/* ============================================================
   ================== EXTENSION FOR LABEL =====================
   ============================================================ */

private fun CreditScore.toReadable(): String = when (this) {
    CreditScore.PEMULA -> "Pemula"
    CreditScore.BAIK -> "Baik"
    CreditScore.SANGAT_BAIK -> "Sangat Baik"
}

private fun BusinessSector.toReadable(): String = when (this) {
    BusinessSector.KULINER -> "Kuliner"
    BusinessSector.FASHION -> "Fashion"
    BusinessSector.RETAIL -> "Retail"
    BusinessSector.JASA -> "Jasa"
    BusinessSector.AGRIKULTUR -> "Agrikultur"
    BusinessSector.KECANTIKAN -> "Kecantikan"
    BusinessSector.KERAJINAN -> "Kerajinan"
    BusinessSector.OTOMOTIF -> "Otomotif"
    BusinessSector.TEKNOLOGI -> "Teknologi"
    BusinessSector.LAINNYA -> "Lainnya"
}

private fun LoanPurpose.toReadable(): String = when (this) {
    LoanPurpose.MODAL_USAHA -> "Modal Usaha"
    LoanPurpose.LAINNYA -> "Tujuan Lain"
}