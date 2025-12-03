package com.example.modalkita.ui.borrower.loan.new

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.modalkita.domain.model.LoanPurpose
import com.example.modalkita.ui.theme.ModalKitaColors

@Composable
fun NewLoanFormScreen(
    viewModel: NewLoanViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF4F5F7)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopBarNewLoan(onBack = onBack)

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                HeaderInfoCard()

                Spacer(Modifier.height(12.dp))

                AmountCard(
                    amountText = state.amountText,
                    onAmountChange = { newText ->
                        // hanya angka & titik
                        val cleaned = newText.filter { it.isDigit() || it == '.' }
                        viewModel.updateAmount(cleaned)
                    }
                )

                Spacer(Modifier.height(10.dp))

                TenorCard(
                    tenorMonths = state.tenorMonths,
                    onTenorSelected = { viewModel.updateTenor(it) }
                )

                Spacer(Modifier.height(10.dp))

                PurposeCard(
                    selectedPurpose = state.purpose,
                    onPurposeSelected = { viewModel.updatePurpose(it) }
                )

                Spacer(Modifier.height(10.dp))

                DescriptionCard(
                    description = state.description,
                    onDescriptionChange = { viewModel.updateDescription(it) }
                )

                Spacer(Modifier.height(10.dp))

                DocumentUploadCard(
                    // TODO: nanti kamu sambungkan dengan picker / uploader
                    onClick = { /* TODO upload dokumen pendukung */ }
                )

                Spacer(Modifier.height(16.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                PrimarySubmitButton(
                    text = "Ajukan Pinjaman",
                    onClick = {
                        // buildSummary akan set error kalau jumlah invalid
                        val ok = viewModel.buildSummary()
                        if (ok) onNext()
                    }
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                    TOP BAR                                 */
/* -------------------------------------------------------------------------- */

@Composable
private fun TopBarNewLoan(
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
                text = "Peminjaman Baru",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                             HEADER INFO CARD                               */
/* -------------------------------------------------------------------------- */

@Composable
private fun HeaderInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModalKitaColors.Green600
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ajukan Peminjaman Baru",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tiap akun ModalKita bisa mengajukan max 3 peminjaman jika skor kredit sangat baik.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE0F2F1)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                           FORM CARD COMPONENTS                             */
/* -------------------------------------------------------------------------- */

@Composable
private fun AmountCard(
    amountText: String,
    onAmountChange: (String) -> Unit
) {
    CardSectionWrapper {
        Text(
            text = "Jumlah Pinjaman",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = amountText,
            onValueChange = onAmountChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            prefix = {
                Text(
                    text = "Rp ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF607D8B)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Min. Rp 5 juta - Max. Rp 500 juta",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFB0BEC5)
            )
        )
    }
}

@Composable
private fun TenorCard(
    tenorMonths: Int,
    onTenorSelected: (Int) -> Unit
) {
    val options = listOf(3, 6, 9, 12, 18, 24)
    val labels = options.map { "$it Bulan" }

    CardSectionWrapper {
        Text(
            text = "Tenor (Bulan)",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdownField(
            valueText = "$tenorMonths Bulan",
            items = labels,
            onItemSelected = { selectedLabel: String ->
                val idx = labels.indexOf(selectedLabel)
                if (idx >= 0) {
                    onTenorSelected(options[idx])
                }
            }
        )
    }
}


@Composable
private fun PurposeCard(
    selectedPurpose: LoanPurpose,
    onPurposeSelected: (LoanPurpose) -> Unit
) {
    val purposes = LoanPurpose.values().toList()
    val labels = purposes.map { it.label }

    CardSectionWrapper {
        Text(
            text = "Tujuan Penggunaan Dana",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
        )

        Spacer(Modifier.height(8.dp))

        SimpleDropdownField(
            valueText = selectedPurpose.label,
            items = labels,
            onItemSelected = { selectedLabel: String ->
                purposes.firstOrNull { it.label == selectedLabel }
                    ?.let(onPurposeSelected)
            }
        )
    }
}



@Composable
private fun DescriptionCard(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    CardSectionWrapper {
        Text(
            text = "Deskripsi Detail",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )
    }
}

@Composable
private fun DocumentUploadCard(
    onClick: () -> Unit
) {
    CardSectionWrapper {
        Text(
            text = "Dokumen Pendukung",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64)
            )
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFE8F0F4))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload",
                    tint = ModalKitaColors.Green600,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Upload Proposal/Portofolio (Max. 5MB)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF607D8B)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               SHARED COMPONENTS                            */
/* -------------------------------------------------------------------------- */


@Composable
private fun CardSectionWrapper(
    content: @Composable ColumnScope.() -> Unit
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
                .padding(horizontal = 18.dp, vertical = 14.dp),
            content = content
        )
    }
}

@Composable
private fun SimpleDropdownField(
    valueText: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Column {
        // THIS PART IS CLICKABLE (GANTIAN TEXTFIELD)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { expanded.value = true }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = "▼",
                    color = Color(0xFF78909C)
                )
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded.value = false
                        onItemSelected(label)
                    }
                )
            }
        }
    }
}


@Composable
private fun PrimarySubmitButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(ModalKitaColors.Green600),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
