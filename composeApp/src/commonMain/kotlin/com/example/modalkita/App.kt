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
import com.example.modalkita.ui.theme.ModalKitaTheme

@Composable
fun App() {
    ModalKitaTheme {

        var isLoggedIn by remember { mutableStateOf(false) }

        // NAVIGASI AWAL
        var startScreen by remember { mutableStateOf("splash") }

        when (startScreen) {

            // SPLASH → ONBOARDING
            "splash" -> SplashScreen(
                onFinished = { startScreen = "onboarding" }
            )

            // ONBOARDING → AUTH FLOW
            "onboarding" -> OnBoardingScreen(
                onNext = { startScreen = "auth" }
            )

            // AUTH / MAIN APP
            "auth" -> {
                AuthFlow(
                    isLoggedIn = isLoggedIn,
                    onLoggedIn = { isLoggedIn = true }
                )
            }
        }
    }
}

/* ============================================================
   ======================= AUTH FLOW =========================
   ============================================================ */

@Composable
fun AuthFlow(
    isLoggedIn: Boolean,
    onLoggedIn: () -> Unit
) {

    var authScreen by remember { mutableStateOf("role") }
    var currentRole by remember { mutableStateOf("") } // "borrower" atau "investor"

    if (isLoggedIn) {

        // ========== MAIN TAB ==========
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
                BottomNavItem.Home -> Text("Home screen", modifier = Modifier.padding(padding))
                BottomNavItem.Funding -> Text("Funding screen", modifier = Modifier.padding(padding))
                BottomNavItem.Loans -> Text("Loans screen", modifier = Modifier.padding(padding))
                BottomNavItem.Profile -> Text("Profile screen", modifier = Modifier.padding(padding))
            }
        }

    } else {

        // ========== AUTH FLOW ==========

        when (authScreen) {

            // PILIH ROLE
            "role" -> PilihRole(
                onBorrowerClicked = {
                    currentRole = "borrower"
                    authScreen = "borrowerIntro"
                },
                onInvestorClicked = {
                    currentRole = "investor"
                    authScreen = "investorIntro"
                }
            )

            // BORROWER INTRO
            "borrowerIntro" -> BorrowerAuth(
                onRegisterClicked = { authScreen = "register" },
                onLoginClicked = { authScreen = "login" }
            )

            // INVESTOR INTRO
            "investorIntro" -> InvestorAuth(
                onRegisterClicked = { authScreen = "register" },
                onLoginClicked = { authScreen = "login" }
            )

            // ---------- LOGIN (BOTH ROLE) ----------
            "login" -> {
                if (currentRole == "borrower") {
                    BorrowerAuthMasuk(
                        onLoginClicked = { onLoggedIn() },
                        onRegisterClicked = { authScreen = "register" },
                        onForgotPasswordClicked = {}
                    )
                } else if (currentRole == "investor") {
                    InvestorAuthMasuk(
                        onLoginClicked = { onLoggedIn() },
                        onRegisterClicked = { authScreen = "register" },
                        onForgotPasswordClicked = {}
                    )
                }
            }

            // ---------- REGISTER (BOTH ROLE) ----------
            "register" -> {
                if (currentRole == "borrower") {
                    BorrowerAuthDaftar(
                        onNextClicked = { authScreen = "registerDetail" },
                        onLoginClicked = { authScreen = "login" }
                    )
                } else if (currentRole == "investor") {
                    InvestorAuthDaftar(
                        onNextClicked = { authScreen = "registerDetail" },
                        onLoginClicked = { authScreen = "login" }
                    )
                }
            }

            // ---------- REGISTER DETAIL (BOTH ROLE) ----------
            "registerDetail" -> {
                if (currentRole == "borrower") {
                    BorrowerAuthDaftar2(
                        onRegisterClicked = { onLoggedIn() },
                        onLoginClicked = { authScreen = "login" }
                    )
                } else if (currentRole == "investor") {
                    InvestorAuthDaftar2(
                        onRegisterClicked = { onLoggedIn() },
                        onLoginClicked = { authScreen = "login" }
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
