package com.example.modalkita.ui.borrower.loan.new

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.theme.ModalKitaColors

@Composable
fun NewLoanSummaryScreen(
    viewModel: NewLoanViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    // kalau submitSuccess true → panggil callback, biar pindah ke Home
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            onSubmitSuccess()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF4F5F7)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBarSummary(onBack = onBack)

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                val summary = state.summary
                if (summary == null) {
                    Text(
                        text = "Data ringkasan pengajuan tidak ditemukan.",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    return@Column
                }

                SummaryHeaderCard(summary)

                Spacer(Modifier.height(12.dp))

                SummaryDetailCard(summary)

                Spacer(Modifier.height(12.dp))

                TermsAndSubmitSection(
                    isSubmitting = state.isSubmitting,
                    errorMessage = state.errorMessage,
                    onSubmit = { viewModel.submitLoan() }
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                   TOP BAR                                  */
/* -------------------------------------------------------------------------- */

@Composable
private fun TopBarSummary(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ModalKitaColors.Green600)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Ringkasan Peminjaman",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                             SUMMARY HEADER CARD                            */
/* -------------------------------------------------------------------------- */

@Composable
private fun SummaryHeaderCard(
    summary: com.example.modalkita.domain.model.NewLoanSummary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModalKitaColors.Green600
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Ringkasan Pengajuan",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Pastikan kembali seluruh informasi sebelum mengajukan peminjaman.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE0F2F1)
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Jumlah Pinjaman",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE0F2F1)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Rp ${summary.principal.formatRupiah()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Tenor",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE0F2F1)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${summary.draft.tenorMonths} Bulan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                             SUMMARY DETAIL CARD                            */
/* -------------------------------------------------------------------------- */

@Composable
private fun SummaryDetailCard(
    summary: com.example.modalkita.domain.model.NewLoanSummary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Detail Peminjaman",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )
            )

            Spacer(Modifier.height(10.dp))

            SummaryRow(
                label = "Jumlah Pokok",
                value = "Rp ${summary.principal.formatRupiah()}"
            )

            SummaryRow(
                label = "Bunga",
                value = "Rp ${summary.interestAmount.formatRupiah()}"
            )

            SummaryRow(
                label = "Total yang Harus Dibayar",
                value = "Rp ${summary.totalToPay.formatRupiah()}",
                isEmphasis = true
            )

            Spacer(Modifier.height(12.dp))

            SummaryRow(
                label = "Cicilan per Bulan",
                value = "Rp ${summary.installmentPerMonth.formatRupiah()}"
            )

            SummaryRow(
                label = "Penalti Keterlambatan / Hari",
                value = "Rp ${summary.penaltyPerDayLate.formatRupiah()}"
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tujuan Penggunaan Dana",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF78909C),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = summary.draft.purpose.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF37474F),
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (summary.draft.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = summary.draft.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF607D8B)
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isEmphasis: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF78909C)
            )
        )
        Text(
            text = value,
            style = if (isEmphasis) {
                MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF263238),
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF455A64)
                )
            }
        )
    }
}

/* -------------------------------------------------------------------------- */
/*                       TERMS + SUBMIT (AJUKAN PINJAMAN)                     */
/* -------------------------------------------------------------------------- */

@Composable
private fun TermsAndSubmitSection(
    isSubmitting: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit
) {
    val isChecked = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { isChecked.value = it }
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Saya menyetujui syarat & ketentuan peminjaman di ModalKita.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF455A64)
                    )
                )
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onSubmit,
                enabled = isChecked.value && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Ajukan Pinjaman",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               HELPER FUNCTION                              */
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
