package com.example.modalkita.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.components.BottomNavItem
import com.example.modalkita.ui.components.ModalKitaBottomBar
import com.example.modalkita.ui.theme.ModalKitaColors

@Composable
fun AuthScreen(
    isLoggedIn: Boolean,
    onLoggedIn: () -> Unit
) {
    var authScreen by remember { mutableStateOf("role") }
    var currentRole by remember { mutableStateOf("") }

    if (isLoggedIn) {
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
        when (authScreen) {
            // PILIH ROLE
            "role" -> PilihRole(
                onBorrowerClicked = {
                    currentRole = "borrower"
                    authScreen = "borrowerLoginRegister"
                },
                onInvestorClicked = {
                    currentRole = "investor"
                    authScreen = "investorLoginRegister"
                }
            )

            // BORROWER LOGIN / REGISTER
            "borrowerLoginRegister" -> BorrowerAuth(
                onRegisterClicked = { authScreen = "borrowerRegisterDetail" },
                onLoginClicked = { authScreen = "borrowerLogin" }
            )

            "borrowerLogin" -> BorrowerAuthMasuk(
                onLoginClicked = { onLoggedIn() },
                onRegisterClicked = { authScreen = "borrowerRegisterDetail" },
                onForgotPasswordClicked = {}
            )

            "borrowerRegisterDetail" -> BorrowerAuthDaftar2(
                onRegisterClicked = { onLoggedIn() },
                onLoginClicked = { authScreen = "borrowerLogin" }
            )

            // INVESTOR LOGIN / REGISTER
            "investorLoginRegister" -> InvestorAuth(
                onRegisterClicked = { authScreen = "investorRegisterDetail" },
                onLoginClicked = { authScreen = "investorLogin" }
            )

            "investorLogin" -> InvestorAuthMasuk(
                onLoginClicked = { onLoggedIn() },
                onRegisterClicked = { authScreen = "investorRegisterDetail" },
                onForgotPasswordClicked = {}
            )

            "investorRegisterDetail" -> InvestorAuthDaftar2(
                onRegisterClicked = { onLoggedIn() },
                onLoginClicked = { authScreen = "investorLogin" }
            )
        }
    }
}
