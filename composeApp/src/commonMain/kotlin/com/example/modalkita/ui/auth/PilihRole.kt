package com.example.modalkita.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.modal_kita_logo
import org.jetbrains.compose.resources.painterResource
import com.example.modalkita.ui.components.PrimaryButton

@Composable
fun PilihRole(
    onBorrowerClicked: () -> Unit,
    onInvestorClicked: () -> Unit,
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
                .padding(top = 32.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(Res.drawable.modal_kita_logo),
                contentDescription = null,
                modifier = Modifier
                    .heightIn(max = 240.dp)
                    .widthIn(max = 240.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Modal Kita",
                style = typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = ModalKitaColors.Green600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Selamat Datang di Modal Kita",
                style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = ModalKitaColors.Green600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Ingin Masuk Sebagai?",
                style = typography.bodyLarge,
                color = ModalKitaColors.Green600
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                label = "Borrower / Peminjam",
                onClick = onBorrowerClicked,
                modifier = Modifier
                    .height(50.dp)
                    .widthIn(260.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Atau",
                style = typography.bodyLarge,
                color = ModalKitaColors.Green600
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = onInvestorClicked,
                modifier = Modifier
                    .height(50.dp)
                    .widthIn(max = 320.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ModalKitaColors.Green600),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ModalKitaColors.Green600
                )
            ) {
                Text(
                    "Investor / Pemberi Dana",
                    style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
