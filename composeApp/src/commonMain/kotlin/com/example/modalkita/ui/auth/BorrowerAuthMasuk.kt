package com.example.modalkita.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import com.example.modalkita.ui.theme.ModalKitaLightColorScheme
import org.jetbrains.compose.resources.painterResource
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.modal_kita_logo

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

// ------------------------------------------

@Composable
fun BorrowerAuthMasuk(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var email by remember { mutableStateOf("Joko1990@gmail.com") }
    var password by remember { mutableStateOf("********") }
    var passwordVisible by remember { mutableStateOf(false) }

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

            // ===== HEADER TITLE =====
            Text(
                text = "Masuk",
                style = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = ModalKitaColors.Black500,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Selamat Datang Kembali",
                style = typography.bodyLarge,
                color = ModalKitaColors.Black300,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            // ===== LOGO/ILUSTRASI =====
            Image(
                painter = painterResource(Res.drawable.modal_kita_logo),
                contentDescription = "Logo Modal Kita",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // ===== EMAIL INPUT =====
            TextFieldLabel("Email")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            // ===== PASSWORD INPUT =====
            TextFieldLabel("Kata Sandi")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = ModalKitaColors.Black300
                        )
                    }
                },
                textStyle = typography.bodyLarge
            )

            // Lupa Password Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPasswordClicked,
                    contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp)
                ) {
                    Text(
                        "Lupa kata sandi?",
                        style = typography.bodyMedium,
                        color = ModalKitaColors.Green600
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

            val isFormValid = email.isNotBlank() && password.isNotBlank()

            PrimaryButton(
                label = "Masuk",
                onClick = onLoginClicked,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(260.dp),
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(15.dp))

            // ===== DAFTAR LINK =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun?",
                    style = typography.bodyMedium,
                    color = ModalKitaColors.Black300
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Daftar",
                    color = ModalKitaColors.Green600,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.clickable { onRegisterClicked() }
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

@Composable
fun BorrowerAuthMasukPreview() {
    ModalKitaTheme {
        BorrowerAuthMasuk(
            onLoginClicked = {},
            onRegisterClicked = {},
            onForgotPasswordClicked = {}
        )
    }
}
