package com.example.modalkita

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.example.modalkita.ui.auth.*
import com.example.modalkita.ui.components.BottomNavItem
import com.example.modalkita.ui.components.ModalKitaBottomBar
import com.example.modalkita.ui.auth.OnBoardingScreen

import com.example.modalkita.ui.auth.SplashScreen
import com.example.modalkita.ui.theme.ModalKitaTheme

enum class StartScreen {
    Splash,
    Onboarding,
    Auth,
    Main
}

@Composable
fun App() {
    ModalKitaTheme {

        var isLoggedIn by remember { mutableStateOf(false) }
        var startScreen by remember { mutableStateOf(StartScreen.Splash) }

        when (startScreen) {
            StartScreen.Splash -> {
                SplashScreen(
                    onFinished = {
                        // Jika auto-login Supabase, cek session di sini
                        startScreen = StartScreen.Onboarding
                    }
                )
            }

            StartScreen.Onboarding -> {
                OnBoardingScreen(
                    onNext = {
                        startScreen = StartScreen.Auth
                    }
                )
            }

            StartScreen.Auth -> {
                AuthFlow(
                    onLoggedIn = {
                        isLoggedIn = true
                        startScreen = StartScreen.Main
                    }
                )
            }

            StartScreen.Main -> {
                MainTabScaffold()
            }
        }
    }
}

/* ======================= MAIN TABS ========================== */

@Composable
private fun MainTabScaffold() {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            ModalKitaBottomBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            BottomNavItem.Home ->
                HomeScreen(modifier = Modifier.padding(padding))

            BottomNavItem.Funding ->
                FundingScreen(modifier = Modifier.padding(padding))

            BottomNavItem.Loans ->
                LoansScreen(modifier = Modifier.padding(padding))

            BottomNavItem.Profile ->
                ProfileScreen(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    Text("Home screen", modifier = modifier)
}

@Composable
private fun FundingScreen(modifier: Modifier = Modifier) {
    Text("Funding screen", modifier = modifier)
}

@Composable
private fun LoansScreen(modifier: Modifier = Modifier) {
    Text("Loans screen", modifier = modifier)
}

@Composable
private fun ProfileScreen(modifier: Modifier = Modifier) {
    Text("Profile screen", modifier = modifier)
}

/* ======================= AUTH FLOW ========================== */

@Composable
fun AuthFlow(
    onLoggedIn: () -> Unit
) {
    var authScreen by remember { mutableStateOf("role") }

    // Menyimpan data dari form step 1 (nama, email, hp, password) untuk borrower
    var borrowerStep1Data by remember { mutableStateOf<BorrowerStep1Data?>(null) }

    when (authScreen) {

        // ==== PILIH ROLE ====
        "role" -> PilihRole(
            onBorrowerClicked = { authScreen = "borrowerIntro" },
            onInvestorClicked = { authScreen = "investorIntro" }
        )

        // ================== BORROWER FLOW ==================

        // Landing / penjelasan Borrower
        "borrowerIntro" -> BorrowerAuthLanding(
            onRegisterClicked = { authScreen = "borrowerRegisterStep1" },
            onLoginClicked = { authScreen = "borrowerLogin" }
        )

        // Login Borrower (pakai LoginScreen generik)
        "borrowerLogin" -> LoginScreen(
            roleLabel = "Peminjam (Borrower)",
            onLoginSuccess = {
                // Login berhasil (AuthViewModel.login sukses)
                onLoggedIn()
            },
            onRegisterClicked = {
                authScreen = "borrowerRegisterStep1"
            }
        )

        // Registrasi Borrower – Step 1 (data akun)
        "borrowerRegisterStep1" -> BorrowerRegisterStep1(
            onNextClicked = { dataFromStep1 ->
                borrowerStep1Data = dataFromStep1
                authScreen = "borrowerRegisterStep2"
            },
            onLoginClicked = {
                authScreen = "borrowerLogin"
            }
        )

        // Registrasi Borrower – Step 2 (data usaha)
        "borrowerRegisterStep2" -> {
            val step1Data = borrowerStep1Data
            if (step1Data != null) {
                BorrowerRegisterStep2(
                    step1Data = step1Data,
                    onRegisterSuccess = {
                        // registerBorrower sukses di AuthViewModel
                        onLoggedIn()
                    },
                    onBackToLogin = {
                        authScreen = "borrowerLogin"
                    }
                )
            } else {
                // Safety fallback: kalau entah bagaimana step1Data null, balik ke step 1
                BorrowerRegisterStep1(
                    onNextClicked = { dataFromStep1 ->
                        borrowerStep1Data = dataFromStep1
                        authScreen = "borrowerRegisterStep2"
                    },
                    onLoginClicked = { authScreen = "borrowerLogin" }
                )
            }
        }

        // ================== INVESTOR FLOW ==================

        // Landing / penjelasan Investor
        "investorIntro" -> InvestorAuthLanding(
            onRegisterClicked = { authScreen = "investorRegister" },
            onLoginClicked = { authScreen = "investorLogin" }
        )

        // Login Investor (pakai LoginScreen yang sama)
        "investorLogin" -> LoginScreen(
            roleLabel = "Investor",
            onLoginSuccess = {
                onLoggedIn()
            },
            onRegisterClicked = {
                authScreen = "investorRegister"
            }
        )

        // Registrasi Investor (satu langkah saja)
        "investorRegister" -> InvestorRegisterScreen(
            onRegisterSuccess = {
                // registerInvestor sukses di AuthViewModel
                onLoggedIn()
            },
            onLoginClicked = {
                authScreen = "investorLogin"
            }
        )
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}