package com.example.modalkita.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography

@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val typography = modalKitaTypography()

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) ModalKitaColors.Green600 else ModalKitaColors.White600, // abu-abu saat disabled
            contentColor = if (enabled) ModalKitaColors.White50 else ModalKitaColors.Black300 // teks gelap saat disabled
        )
    ) {
        Text(
            text = label,
            style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}
