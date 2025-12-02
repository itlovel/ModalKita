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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import com.example.modalkita.ui.theme.ModalKitaLightColorScheme

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ModalKitaColors.Green600,
    unfocusedBorderColor = ModalKitaColors.Green600,
    cursorColor = ModalKitaColors.Green600,
    focusedContainerColor = Color(0xFFF0F9F7),
    unfocusedContainerColor = Color(0xFFF0F9F7)
)

@Composable
private fun ModalKitaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ModalKitaLightColorScheme,
        typography = modalKitaTypography(),
        content = content
    )
}

@Composable
fun BorrowerAuthDaftar2(
    onRegisterClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var businessName by remember { mutableStateOf("Sop Iga Babi Joko") }
    var city by remember { mutableStateOf("Malang") }

    val sectors = listOf("Kuliner / Makanan & Minuman", "Fashion & Apparel", "Retail / Toko Kelontong", "Jasa", "Agrikultur / Pertanian / Peternakan", "Kecantikan & Kesehatan (Beauty/Health)", "Kerajinan / Handmade", "Otomotif", "Teknologi / IT Services", "Lainnya")
    var selectedSector by remember { mutableStateOf(sectors[0]) }
    var sectorExpanded by remember { mutableStateOf(false) }

    val durations = listOf("< 6 Bulan", "6–12 Bulan", "1–3 Tahun", "> 3 Tahun")
    var selectedDuration by remember { mutableStateOf(durations[0]) }
    var durationExpanded by remember { mutableStateOf(false) }

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
                text = "Daftar",
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

            // Nama Usaha
            TextFieldLabel("Nama Usaha")
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                textStyle = typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(15.dp))

            // Kota/ Domisili
            TextFieldLabel("Kota/Domisili Usaha")
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                textStyle = typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(15.dp))

            // Sektor Usaha (Dropdown)
            TextFieldLabel("Sektor Usaha")
            ExposedDropdownSelector(
                items = sectors,
                selectedItem = selectedSector,
                onItemSelected = { selectedSector = it },
                expanded = sectorExpanded,
                onExpandedChange = { sectorExpanded = it }
            )
            Spacer(modifier = Modifier.height(15.dp))

            // Lama Usaha (Dropdown)
            TextFieldLabel("Lama Usaha")
            ExposedDropdownSelector(
                items = durations,
                selectedItem = selectedDuration,
                onItemSelected = { selectedDuration = it },
                expanded = durationExpanded,
                onExpandedChange = { durationExpanded = it }
            )

            Spacer(modifier = Modifier.height(35.dp))

            val isFormValid = businessName.isNotBlank() &&
                    city.isNotBlank() &&
                    selectedSector.isNotBlank() &&
                    selectedDuration.isNotBlank()

            PrimaryButton(
                label = "Daftar",
                onClick = onRegisterClicked,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(260.dp),
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun?",
                    style = typography.bodyMedium,
                    color = ModalKitaColors.Black300
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Masuk",
                    color = ModalKitaColors.Green600,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.clickable { onLoginClicked() }
                )
            }
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
            colors = textFieldColors(),
            shape = RoundedCornerShape(12.dp),
            textStyle = modalKitaTypography().bodyLarge
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, style = modalKitaTypography().bodyLarge) },
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

@Composable
fun BorrowerAuthDaftar2Preview() {
    ModalKitaTheme {
        BorrowerAuthDaftar2(
            onRegisterClicked = {},
            onLoginClicked = {}
        )
    }
}
