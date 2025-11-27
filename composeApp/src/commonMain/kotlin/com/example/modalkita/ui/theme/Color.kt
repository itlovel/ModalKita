package com.example.modalkita.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object ModalKitaColors {

    // Green
    val Green50  = Color(0xFFE9EDEC)
    val Green100 = Color(0xFFBAC7C3)
    val Green200 = Color(0xFF9DABA5)
    val Green300 = Color(0xFF6A857C)
    val Green400 = Color(0xFF4D6D63)
    val Green500 = Color(0xFF20493C)
    val Green600 = Color(0xFF1D4237)
    val Green700 = Color(0xFF17342B)
    val Green800 = Color(0xFF122821)
    val Green900 = Color(0xFF0D1719)

    // Light Green
    val LightGreen50  = Color(0xFFF0F9F7)
    val LightGreen100 = Color(0xFFD1EDE5)
    val LightGreen200 = Color(0xFFBAE5D9)
    val LightGreen300 = Color(0xFF9BD9C7)
    val LightGreen400 = Color(0xFF88D1BD)
    val LightGreen500 = Color(0xFF6AC8AC)
    val LightGreen600 = Color(0xFF60B49D)
    val LightGreen700 = Color(0xFF4B8D7A)
    val LightGreen800 = Color(0xFF3A6D5F)
    val LightGreen900 = Color(0xFF2D5348)

    // White scale
    val White50  = Color(0xFFFFFFFF)
    val White100 = Color(0xFFFFFFFF)
    val White200 = Color(0xFFFFFFFF)
    val White300 = Color(0xFFFFFFFF)
    val White400 = Color(0xFFFFFFFF)
    val White500 = Color(0xFFFFFFFF)
    val White600 = Color(0xFFE8E8E8)
    val White700 = Color(0xFFB5B5B5)
    val White800 = Color(0xFF8C8C8C)
    val White900 = Color(0xFF6B6B6B)

    // Black scale
    val Black50  = Color(0xFFE6E6E6)
    val Black100 = Color(0xFFB0B0B0)
    val Black200 = Color(0xFF8A8A8A)
    val Black300 = Color(0xFF545454)
    val Black400 = Color(0xFF333333)
    val Black500 = Color(0xFF000000)
    val Black600 = Color(0xFF000000)
    val Black700 = Color(0xFF000000)
    val Black800 = Color(0xFF000000)
    val Black900 = Color(0xFF000000)
}

// Color scheme utama (bisa kamu tweak kapan-kapan)
val ModalKitaLightColorScheme = lightColorScheme(
    primary = ModalKitaColors.Green600,
    onPrimary = ModalKitaColors.White50,
    secondary = ModalKitaColors.LightGreen500,
    onSecondary = ModalKitaColors.Black500,
    background = ModalKitaColors.White50,
    onBackground = ModalKitaColors.Black500,
    surface = ModalKitaColors.White50,
    onSurface = ModalKitaColors.Black500,
    error = Color(0xFFB3261E),
    onError = ModalKitaColors.White50,
)
