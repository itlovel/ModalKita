package com.example.modalkita.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.modal_kita_logo
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography

@Composable
fun SplashScreen(
    onFinished: () -> Unit,   // ← GANTI INI
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    // ⏳ Auto navigate after 2 sec
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()          // ← Panggil callback
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ModalKitaColors.White50
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Image(
                    painter = painterResource(Res.drawable.modal_kita_logo),
                    contentDescription = "Logo Modal Kita",
                    modifier = Modifier.size(220.dp)
                )

                Spacer(Modifier.height(8.dp))

                androidx.compose.material3.Text(
                    text = "Modal Kita",
                    style = typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                    color = ModalKitaColors.Green600
                )
            }
        }
    }
}
