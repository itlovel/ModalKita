package com.example.modalkita

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.modalkita.ui.components.BottomNavItem
import com.example.modalkita.ui.components.ModalKitaBottomBar
import com.example.modalkita.ui.theme.ModalKitaTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    ModalKitaTheme {
        var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }

        Scaffold(
            bottomBar = {
                ModalKitaBottomBar(
                    selectedItem = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            // Sementara: konten dummy per tab
            when (selectedTab) {
                BottomNavItem.Home ->
                    Text(
                        text = "Home screen",
                        modifier = Modifier.padding(innerPadding)
                    )

                BottomNavItem.Funding ->
                    Text(
                        text = "Funding screen",
                        modifier = Modifier.padding(innerPadding)
                    )

                BottomNavItem.Loans ->
                    Text(
                        text = "Loans screen",
                        modifier = Modifier.padding(innerPadding)
                    )

                BottomNavItem.Profile ->
                    Text(
                        text = "Profile screen",
                        modifier = Modifier.padding(innerPadding)
                    )
            }
        }
    }
}

// Preview di Android Studio (tidak wajib untuk iOS)
@Preview
@Composable
fun AppPreview() {
    App()
}