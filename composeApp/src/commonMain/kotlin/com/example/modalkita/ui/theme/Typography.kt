package com.example.modalkita.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.Poppins_Regular
import modalkita.composeapp.generated.resources.Poppins_SemiBold
import modalkita.composeapp.generated.resources.Poppins_Bold

@OptIn(ExperimentalResourceApi::class)
@Composable
fun modalKitaTypography() : Typography {

    val poppins = FontFamily(
        Font(Res.font.Poppins_Regular, weight = FontWeight.Normal),
        Font(Res.font.Poppins_SemiBold, weight = FontWeight.SemiBold),
        Font(Res.font.Poppins_Bold,    weight = FontWeight.Bold),
    )

    // Mapping dari Figma:
    // H1 = 30px, H2 = 25px, Medium = 20px, Base = 16px, Small = 12px, XS = 10px

    return Typography(
        displayLarge = TextStyle(      // H1
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
            lineHeight = 36.sp
        ),
        headlineLarge = TextStyle(     // H2
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 25.sp,
            lineHeight = 30.sp
        ),
        titleLarge = TextStyle(        // Medium
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 24.sp
        ),
        bodyLarge = TextStyle(         // Base
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 19.2.sp
        ),
        bodyMedium = TextStyle(        // Small
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 14.4.sp
        ),
        bodySmall = TextStyle(         // XS
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 12.sp
        ),
    )
}