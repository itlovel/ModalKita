package com.example.modalkita

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.modalkita.ui.auth.BorrowerStep1Data
import com.example.modalkita.ui.auth.BorrowerAuthLanding
import com.example.modalkita.ui.auth.BorrowerRegisterStep1
import com.example.modalkita.ui.auth.BorrowerRegisterStep2
import com.example.modalkita.ui.auth.InvestorAuthLanding
import com.example.modalkita.ui.auth.InvestorRegisterScreen
import com.example.modalkita.ui.auth.LoginScreen
import com.example.modalkita.ui.auth.PilihRole
import com.example.modalkita.ui.components.BottomNavItem
import com.example.modalkita.ui.components.ModalKitaBottomBar
import com.example.modalkita.ui.funding.InvestorFundingRoot
import com.example.modalkita.ui.investment.InvestorInvestmentRoot
import com.example.modalkita.ui.profile.InvestorProfileRoot
import com.example.modalkita.ui.theme.ModalKitaTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/* ======================= USER ROLE ========================== */

enum class UserRole {
    BORROWER,
    INVESTOR
}

/* ======================= APP ROOT ========================== */

@Composable
fun App(
    openMidtransPayment: (String) -> Unit
) {
    ModalKitaTheme {

        // null = belum login, non-null = sudah login & tahu role-nya
        var currentRole by remember { mutableStateOf<UserRole?>(null) }

        if (currentRole == null) {
            // Flow pilih role + login/register
            AuthFlow(
                onLoggedIn = { role ->
                    currentRole = role
                }
            )
        } else {
            // Setelah login, masuk ke layar utama yang pakai bottom nav
            MainScreen(
                role = currentRole!!,
                openMidtransPayment = openMidtransPayment,
                onLoggedOut = {
                    // Reset ke state awal (kembali ke AuthFlow)
                    currentRole = null
                }
            )
        }
    }
}

/* ======================= MAIN SCREEN + BOTTOM BAR ========================== */

@Composable
fun MainScreen(
    role: UserRole,
    openMidtransPayment: (String) -> Unit,
    onLoggedOut: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(BottomNavItem.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ModalKitaBottomBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->

        when (selectedItem) {

            BottomNavItem.Home -> {
                Text(
                    text = when (role) {
                        UserRole.INVESTOR -> "Home Investor (dashboard investor nanti di sini)"
                        UserRole.BORROWER -> "Home Borrower (dashboard borrower nanti di sini)"
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            BottomNavItem.Funding -> {
                if (role == UserRole.INVESTOR) {
                    // HANYA investor yang bisa akses fitur pendanaan + Midtrans
                    InvestorFundingRoot(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onOpenPaymentLink = openMidtransPayment
                    )
                } else {
                    Text(
                        text = "Fitur pendanaan hanya bisa diakses oleh Investor.",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }

            BottomNavItem.Loans -> {
                // Di desain awal: tab ini kamu pakai sebagai halaman "Investasi Saya"
                // untuk INVESTOR. Kalau mau dibedakan untuk borrower, tinggal if role.
                InvestorInvestmentRoot(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onOpenPaymentLink = openMidtransPayment
                )
            }

            BottomNavItem.Profile -> {
                InvestorProfileRoot(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onLoggedOut = {
                        onLoggedOut()
                        // Balik tab ke Home supaya aman
                        selectedItem = BottomNavItem.Home
                    }
                )
            }
        }
    }
}

/* ======================= AUTH FLOW ========================== */

@Composable
fun AuthFlow(
    onLoggedIn: (UserRole) -> Unit
) {
    var authScreen by remember { mutableStateOf("role") }

    var borrowerStep1Data by remember { mutableStateOf<BorrowerStep1Data?>(null) }

    when (authScreen) {

        // ==== PILIH ROLE ====
        "role" -> PilihRole(
            onBorrowerClicked = { authScreen = "borrowerIntro" },
            onInvestorClicked = { authScreen = "investorIntro" }
        )

        // ================== BORROWER FLOW ==================

        "borrowerIntro" -> BorrowerAuthLanding(
            onRegisterClicked = { authScreen = "borrowerRegisterStep1" },
            onLoginClicked = { authScreen = "borrowerLogin" }
        )

        "borrowerLogin" -> LoginScreen(
            roleLabel = "Peminjam (Borrower)",
            onLoginSuccess = {
                onLoggedIn(UserRole.BORROWER)
            },
            onRegisterClicked = {
                authScreen = "borrowerRegisterStep1"
            }
        )

        "borrowerRegisterStep1" -> BorrowerRegisterStep1(
            onNextClicked = { dataFromStep1: BorrowerStep1Data ->
                borrowerStep1Data = dataFromStep1
                authScreen = "borrowerRegisterStep2"
            },
            onLoginClicked = {
                authScreen = "borrowerLogin"
            }
        )

        "borrowerRegisterStep2" -> {
            val step1Data: BorrowerStep1Data? = borrowerStep1Data
            if (step1Data != null) {
                BorrowerRegisterStep2(
                    step1Data = step1Data,
                    onRegisterSuccess = {
                        onLoggedIn(UserRole.BORROWER)
                    },
                    onBackToLogin = {
                        authScreen = "borrowerLogin"
                    }
                )
            } else {
                BorrowerRegisterStep1(
                    onNextClicked = { dataFromStep1: BorrowerStep1Data ->
                        borrowerStep1Data = dataFromStep1
                        authScreen = "borrowerRegisterStep2"
                    },
                    onLoginClicked = { authScreen = "borrowerLogin" }
                )
            }
        }

        // ================== INVESTOR FLOW ==================

        "investorIntro" -> InvestorAuthLanding(
            onRegisterClicked = { authScreen = "investorRegister" },
            onLoginClicked = { authScreen = "investorLogin" }
        )

        "investorLogin" -> LoginScreen(
            roleLabel = "Investor",
            onLoginSuccess = {
                onLoggedIn(UserRole.INVESTOR)
            },
            onRegisterClicked = {
                authScreen = "investorRegister"
            }
        )

        "investorRegister" -> InvestorRegisterScreen(
            onRegisterSuccess = {
                onLoggedIn(UserRole.INVESTOR)
            },
            onLoginClicked = {
                authScreen = "investorLogin"
            }
        )
    }
}

/* ================== PREVIEW ======================= */

@Preview
@Composable
fun AppPreview() {
    App(openMidtransPayment = { /* no-op */ })
}