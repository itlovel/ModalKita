package com.example.modalkita.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

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
