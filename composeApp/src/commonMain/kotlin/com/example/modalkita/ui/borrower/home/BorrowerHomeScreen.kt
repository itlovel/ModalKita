package com.example.modalkita.ui.borrower.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.model.CreditCategory
import com.example.modalkita.domain.model.LoanApplicationStatus

@Composable
fun BorrowerHomeScreen(
    viewModel: BorrowerHomeViewModel,
    onNewApplicationClick: () -> Unit,
    onApplicationDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> LoadingView()
        state.errorMessage != null -> ErrorView(state.errorMessage!!)
        state.dashboard != null -> BorrowerHomeContent(
            dashboard = state.dashboard!!,
            onNewApplicationClick = onNewApplicationClick,
            onApplicationDetailClick = onApplicationDetailClick
        )
    }
}

@Composable
private fun BorrowerHomeContent(
    dashboard: BorrowerHomeDashboard,
    onNewApplicationClick: () -> Unit,
    onApplicationDetailClick: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        BorrowerGreetingCard(username = dashboard.username)

        CreditScoreCard(
            score = dashboard.creditScore,
            category = dashboard.creditCategory
        )

        ApplicationStatusCard(
            dashboard = dashboard,
            onNewApplicationClick = onNewApplicationClick,
            onDetailClick = onApplicationDetailClick
        )
    }
}

@Composable
fun BorrowerGreetingCard(
    username: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Siap merintis hari ini?", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "@$username",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF4CAF50) // hijau username
            )
        }
    }
}

@Composable
fun CreditScoreCard(
    score: Int,
    category: CreditCategory,
    modifier: Modifier = Modifier
) {
    val label = when (category) {
        CreditCategory.SANGAT_BAIK -> "Sangat Baik"
        CreditCategory.BAIK        -> "Baik"
        CreditCategory.KURANG      -> "Kurang"
    }

    val badgeColor = when (category) {
        CreditCategory.SANGAT_BAIK -> Color(0xFF2E7D32)
        CreditCategory.BAIK        -> Color(0xFF388E3C)
        CreditCategory.KURANG      -> Color(0xFFF57C00)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Kredit", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(badgeColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (category == CreditCategory.KURANG)
                    "Skor kredit anda belum memenuhi untuk mengajukan pinjaman."
                else
                    "Skor kredit anda akan mempengaruhi pengajuan anda berikutnya.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ApplicationStatusCard(
    dashboard: BorrowerHomeDashboard,
    onNewApplicationClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Text(
                text = "Status Pengajuan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            when (dashboard.activeApplicationStatus) {

                LoanApplicationStatus.NONE -> {
                    Text(
                        text = "Belum Ada Pengajuan",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(16.dp))

                    IconButton(
                        onClick = {
                            if (dashboard.canApply) onNewApplicationClick()
                        },
                        enabled = dashboard.canApply
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (dashboard.canApply) Color(0xFF2E7D32) else Color.LightGray
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Pengajuan",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = if (dashboard.canApply)
                            "Tambah Pengajuan Baru?"
                        else
                            "Anda belum dapat mengajukan pinjaman.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                LoanApplicationStatus.PENINJAUAN -> {
                    Text("Peninjauan Sistem", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Pengajuan Ditinjau Oleh Sistem",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryDetailButton(onDetailClick)
                }

                LoanApplicationStatus.PENDANAAN,
                LoanApplicationStatus.SIAP_DICAIRKAN -> {
                    val fundedPercent = dashboard.fundedPercentage
                    val fundedAmount = dashboard.fundedAmount ?: 0L
                    val totalAmount = dashboard.loanAmount ?: 0L

                    Text(
                        text = if (dashboard.activeApplicationStatus == LoanApplicationStatus.PENDANAAN)
                            "Pendanaan"
                        else
                            "Dana Sudah Siap Dicairkan",
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${fundedPercent}% didanai   •   ${dashboard.investorCount} Investor",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(16.dp))

                    // Untuk sementara pakai text, nanti bisa diganti donut chart
                    Text(
                        text = "Rp ${fundedAmount.formatRupiah()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "dari Rp ${totalAmount.formatRupiah()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(16.dp))
                    PrimaryDetailButton(onDetailClick)
                }
            }
        }
    }
}

@Composable
private fun PrimaryDetailButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Detail")
    }
}

/**
 * Formatter rupiah sederhana, aman untuk commonMain (tanpa java.text / String.format)
 */
private fun Long.formatRupiah(): String {
    val raw = this.toString()
    if (raw.length <= 3) return raw

    val sb = StringBuilder()
    var count = 0

    for (i in raw.length - 1 downTo 0) {
        sb.append(raw[i])
        count++
        if (count == 3 && i != 0) {
            sb.append('.')
            count = 0
        }
    }
    return sb.reverse().toString()
}

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Red)
    }
}
