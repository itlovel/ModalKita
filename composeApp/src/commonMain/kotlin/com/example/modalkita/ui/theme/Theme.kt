package com.example.modalkita.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.modalkita.ui.theme.ModalKitaLightColorScheme
import com.example.modalkita.ui.theme.modalKitaTypography

@Composable
fun ModalKitaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ModalKitaLightColorScheme,
        typography = modalKitaTypography(),
        content = content
    )
}
