package com.example.modalkita

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.modalkita.ui.components.BottomNavItem
import com.example.modalkita.ui.components.ModalKitaBottomBar
import com.example.modalkita.ui.components.ModalKitaTopBar
import com.example.modalkita.ui.theme.ModalKitaTheme

// resource avatar (pakai ikon profile yang sudah ada dulu)
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.ic_profile

@Composable
fun App() {
    ModalKitaTheme {
        var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }

        // Title + subtitle dinamis per tab
        val (title, subtitle) = when (selectedTab) {
            BottomNavItem.Home ->
                "Dashboard" to "Kelola pendanaan dan pinjaman Anda"
            BottomNavItem.Funding ->
                "Pendanaan" to "Daftar peluang pendanaan aktif"
            BottomNavItem.Loans ->
                "Pinjaman" to "Status pinjaman UMKM Anda"
            BottomNavItem.Profile ->
                "Profil" to "Kelola data akun dan keamanan"
        }

        Scaffold(
//            topBar = {
//                ModalKitaChildTopBar(
//                    title = title,
//                    subtitle = subtitle,
//                    profileImage = Res.drawable.ic_profile,
//                    onProfileClick = { selectedTab = BottomNavItem.Profile }
//                )
//            },
            bottomBar = {
                ModalKitaBottomBar(
                    selectedItem = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            // Konten sementara per tab
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

@Preview
@Composable
fun AppPreview() {
    App()
}
