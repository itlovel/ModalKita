package com.example.modalkita.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.data.auth.BorrowerRegisterData
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import kotlinx.coroutines.launch

@Composable
fun BorrowerRegisterStep2(
    step1Data: BorrowerStep1Data,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()
    val viewModel = remember { AuthViewModel() }
    val scope = rememberCoroutineScope()

    var businessName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    val sectors = listOf(
        "Kuliner / Makanan & Minuman",
        "Fashion & Apparel",
        "Retail / Toko Kelontong",
        "Jasa",
        "Agrikultur / Pertanian / Peternakan",
        "Kecantikan & Kesehatan",
        "Kerajinan / Handmade",
        "Otomotif",
        "Teknologi / IT Services",
        "Lainnya"
    )
    var selectedSector by remember { mutableStateOf(sectors[0]) }
    var sectorExpanded by remember { mutableStateOf(false) }

    val durations = listOf("< 6 Bulan", "6–12 Bulan", "1–3 Tahun", "> 3 Tahun")
    var selectedDuration by remember { mutableStateOf(durations[0]) }
    var durationExpanded by remember { mutableStateOf(false) }

    val isFormValid =
        businessName.isNotBlank() &&
                city.isNotBlank() &&
                selectedSector.isNotBlank() &&
                selectedDuration.isNotBlank()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ModalKitaColors.White50
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Daftar Borrower",
                style = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = ModalKitaColors.Black500,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lengkapi deskripsi usaha anda",
                style = typography.bodyLarge,
                color = ModalKitaColors.Black300,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(35.dp))

            TextFieldLabel("Nama Usaha")
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                textStyle = typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(15.dp))

            TextFieldLabel("Kota/Domisili Usaha")
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                textStyle = typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(15.dp))

            TextFieldLabel("Sektor Usaha")
            ExposedDropdownSelector(
                items = sectors,
                selectedItem = selectedSector,
                onItemSelected = { selectedSector = it },
                expanded = sectorExpanded,
                onExpandedChange = { sectorExpanded = it }
            )
            Spacer(modifier = Modifier.height(15.dp))

            TextFieldLabel("Lama Usaha")
            ExposedDropdownSelector(
                items = durations,
                selectedItem = selectedDuration,
                onItemSelected = { selectedDuration = it },
                expanded = durationExpanded,
                onExpandedChange = { durationExpanded = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (viewModel.successMessage != null) {
                Text(
                    text = viewModel.successMessage.orEmpty(),
                    color = ModalKitaColors.Green600,
                    style = typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            PrimaryButton(
                label = if (viewModel.isLoading) "Memproses..." else "Daftar",
                enabled = isFormValid && !viewModel.isLoading,
                onClick = {
                    scope.launch {
                        val input = BorrowerRegisterData(
                            fullName = step1Data.fullName,
                            email = step1Data.email,
                            phone = step1Data.phone,
                            password = step1Data.password,
                            businessName = businessName,
                            city = city,
                            sector = selectedSector,
                            businessDuration = selectedDuration
                        )
                        viewModel.registerBorrower(input)
                        if (viewModel.errorMessage == null) {
                            onRegisterSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(260.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Sudah punya akun? Masuk",
                style = typography.bodyMedium,
                color = ModalKitaColors.Green600,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}

@Composable
private fun TextFieldLabel(text: String) {
    val typography = modalKitaTypography()
    Text(
        text = text,
        style = typography.bodyLarge,
        color = ModalKitaColors.Black500,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val typography = modalKitaTypography()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = borrowerTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            textStyle = typography.bodyLarge
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, style = typography.bodyLarge) },
                    onClick = {
                        onItemSelected(selectionOption)
                        onExpandedChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}