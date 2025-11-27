package com.example.modalkita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.theme.ModalKitaColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Sesuaikan import Res dan nama resource dengan project-mu
import modalkita.composeapp.generated.resources.Res
import modalkita.composeapp.generated.resources.ic_home
import modalkita.composeapp.generated.resources.ic_funding
import modalkita.composeapp.generated.resources.ic_loans
import modalkita.composeapp.generated.resources.ic_profile

@Immutable
enum class BottomNavItem(
    val label: String,
    val iconRes: DrawableResource
) {
    Home("Home", Res.drawable.ic_home),
    Funding("Funding", Res.drawable.ic_funding),
    Loans("Loans", Res.drawable.ic_loans),
    Profile("Profile", Res.drawable.ic_profile)
}

@Composable
fun ModalKitaBottomBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dibungkus Surface supaya bisa rounded hanya di bagian atas
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = ModalKitaColors.Green600,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.values().forEach { item ->
                val isSelected = item == selectedItem
                val tint = if (isSelected) {
                    ModalKitaColors.LightGreen400   // icon aktif = light green
                } else {
                    ModalKitaColors.White50         // icon nonaktif = putih
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(tint)
                    )
                }
            }
        }
    }
}
