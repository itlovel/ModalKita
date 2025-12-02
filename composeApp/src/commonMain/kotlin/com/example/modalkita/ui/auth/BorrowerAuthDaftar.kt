package com.example.modalkita.ui.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.components.PrimaryButton
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography

data class BorrowerStep1Data(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
)

@Composable
fun BorrowerRegisterStep1(
    onNextClicked: (BorrowerStep1Data) -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = modalKitaTypography()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isFormValid =
        fullName.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                password == confirmPassword

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
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Daftar",
                style = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = ModalKitaColors.Green600,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Buat akun borrower anda!",
                style = typography.bodyLarge,
                color = ModalKitaColors.Green600,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text("Nama Lengkap", style = typography.bodyLarge, color = ModalKitaColors.Green600)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text("Email", style = typography.bodyLarge, color = ModalKitaColors.Green600)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text("Nomor Telepon", style = typography.bodyLarge, color = ModalKitaColors.Green600)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text("Kata Sandi", style = typography.bodyLarge, color = ModalKitaColors.Green600)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text("Konfirmasi Kata Sandi", style = typography.bodyLarge, color = ModalKitaColors.Green600)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                colors = borrowerTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(35.dp))

            PrimaryButton(
                label = "Selanjutnya",
                enabled = isFormValid,
                onClick = {
                    onNextClicked(
                        BorrowerStep1Data(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            password = password
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(50.dp)
                    .widthIn(260.dp),
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
fun borrowerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ModalKitaColors.Green600,
    unfocusedBorderColor = ModalKitaColors.Green600,
    cursorColor = ModalKitaColors.Green600,
    focusedContainerColor = Color(0xFFF0F9F7),
    unfocusedContainerColor = Color(0xFFF0F9F7)
)
