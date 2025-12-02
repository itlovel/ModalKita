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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.modalkita.ui.theme.ModalKitaColors

@Composable
fun BorrowerHomeScreen(
    viewModel: BorrowerHomeViewModel,
    onNewApplicationClick: () -> Unit,
    onApplicationDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF4F5F7) // abu-abu muda seperti background figma
    ) {
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null && state.dashboard == null -> ErrorView(state.errorMessage!!)
            state.dashboard != null -> BorrowerHomeContent(
                dashboard = state.dashboard!!,
                onNewApplicationClick = onNewApplicationClick,
                onApplicationDetailClick = onApplicationDetailClick
            )
        }
    }
}

@Composable
private fun BorrowerHomeContent(
    dashboard: BorrowerHomeDashboard,
    onNewApplicationClick: () -> Unit,
    onApplicationDetailClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header hijau + greeting card yang nempel
            BorrowerHeaderSection(username = dashboard.username)

            Spacer(Modifier.height(12.dp))

            CreditScoreCard(
                score = dashboard.creditScore,
                category = dashboard.creditCategory
            )

            ApplicationStatusCard(
                dashboard = dashboard,
                onNewApplicationClick = onNewApplicationClick,
                onDetailClick = onApplicationDetailClick
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               HEADER & GREET                               */
/* -------------------------------------------------------------------------- */

@Composable
private fun BorrowerHeaderSection(
    username: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // background hijau atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    color = ModalKitaColors.Green600,
                    shape = RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ModalKita",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Peminjam",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFE0F2F1),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // greeting card putih yang "mengambang"
        BorrowerGreetingCard(
            username = username,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 80.dp) // sedikit lebih naik biar mirip Figma
        )
    }

    Spacer(Modifier.height(70.dp)) // ruang di bawah greeting card
}


@Composable
fun BorrowerGreetingCard(
    username: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                "Siap merintis hari ini?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF455A64),
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "@$username",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = ModalKitaColors.LightGreen400
            )
        }
    }
}


/* -------------------------------------------------------------------------- */
/*                               CREDIT SCORE CARD                            */
/* -------------------------------------------------------------------------- */

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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModalKitaColors.Green600
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                "Kredit",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Skor kredit anda akan mempengaruhi pengajuan anda berikutnya.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE0F2F1)
                )
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                          APPLICATION STATUS CARD                           */
/* -------------------------------------------------------------------------- */

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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Status Pengajuan",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )
            )

            Spacer(Modifier.height(4.dp))

            when (dashboard.activeApplicationStatus) {

                LoanApplicationStatus.NONE -> {
                    Text(
                        text = "Belum Ada Pengajuan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF455A64)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0% didanai",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF90A4AE)
                            )
                        )
                        Text(
                            text = "0 Investor",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF90A4AE)
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    IconButton(
                        onClick = {
                            if (dashboard.canApply) onNewApplicationClick()
                        },
                        enabled = dashboard.canApply
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (dashboard.canApply)
                                        ModalKitaColors.Green600
                                    else
                                        Color.LightGray
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
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF607D8B)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                LoanApplicationStatus.PENINJAUAN -> {
                    Text(
                        "Peninjauan Sistem",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF455A64)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Pengajuan Ditinjau Oleh Sistem",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF90A4AE)
                        ),
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
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF455A64)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${fundedPercent}% didanai   •   ${dashboard.investorCount} Investor",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF90A4AE)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Rp ${fundedAmount.formatRupiah()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                    )
                    Text(
                        text = "dari Rp ${totalAmount.formatRupiah()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF90A4AE)
                        )
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
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Detail")
    }
}

/* -------------------------------------------------------------------------- */
/*                             Helper & State Views                            */
/* -------------------------------------------------------------------------- */

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
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Red)
    }
}
