package com.example.modalkita.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.borrower_auth_illustration
import org.jetbrains.compose.resources.painterResource
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import com.example.modalkita.ui.components.PrimaryButton

@Composable
fun InvestorAuth(
    onRegisterClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ModalKitaColors.White50
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ======= TOP CONTENT =======
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.borrower_auth_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .heightIn(max = 342.dp)
                        .widthIn(max = 456.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Autentikasi Investor",
                    style = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = ModalKitaColors.Green600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Investor adalah pengguna yang memberikan pendanaan pada mitra UMKM untuk mendapatkan imbal hasil yang aman, transparan, dan menguntungkan.",
                    style = typography.bodyMedium,
                    color = ModalKitaColors.Green600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            // ======= BUTTON SECTION =======
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Belum Memiliki Akun?",
                    style = typography.bodyLarge,
                    color = ModalKitaColors.Green600
                )

                Spacer(modifier = Modifier.height(15.dp))

                PrimaryButton(
                    label = "Daftar",
                    onClick = onRegisterClicked,
                    modifier = Modifier
                        .height(50.dp)
                        .widthIn(260.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Atau",
                    style = typography.bodyLarge,
                    color = ModalKitaColors.Green600
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = onLoginClicked,
                    modifier = Modifier
                        .height(50.dp)
                        .widthIn(260.dp),
                    border = BorderStroke(1.dp, ModalKitaColors.Green600),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ModalKitaColors.Green600
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Masuk",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}