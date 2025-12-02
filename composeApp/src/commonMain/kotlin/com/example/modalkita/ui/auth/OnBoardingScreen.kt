package com.example.modalkita.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modalkita.ui.theme.ModalKitaColors
import com.example.modalkita.ui.theme.modalKitaTypography
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import modalkita.composeapp.generated.resources.*

@Composable
fun OnBoardingScreen(
    onNext: () -> Unit
) {
    val typography = modalKitaTypography()
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnBoardingPage(
            image = Res.drawable.onboarding1,
            title = "Akses Modal Mudah untuk UMKM!",
            desc = "Bangun usahamu tanpa ribet. Ajukan pinjaman langsung dari smartphone, cepat dan sederhana."
        ),
        OnBoardingPage(
            image = Res.drawable.onboarding2,
            title = "Transparansi yang Tidak Bisa Dimanipulasi.",
            desc = "Catatan transaksi immutable dengan smart contract."
        ),
        OnBoardingPage(
            image = Res.drawable.onboarding3,
            title = "Investasi Berdampak, Aman & Terukur",
            desc = "Kembangkan portofolio sambil membantu UMKM tumbuh."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(containerColor = ModalKitaColors.White50) { padding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->

            val data = pages[page]

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // === IMAGE TOP ===
                Image(
                    painter = painterResource(data.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.65f)
                )

                // === GREEN CARD ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f)
                        .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                        .background(ModalKitaColors.Green700)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // TITLE
                        Text(
                            text = data.title,
                            style = typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = ModalKitaColors.White50
                        )

                        Spacer(Modifier.height(12.dp))

                        // DESCRIPTION
                        Text(
                            text = data.desc,
                            style = typography.bodyMedium,
                            color = ModalKitaColors.White50
                        )

                        Spacer(Modifier.weight(1f))

                        // === INDICATOR (BAR + DOT) ===
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(pages.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .height(8.dp)
                                        .width(
                                            if (pagerState.currentPage == index)
                                                22.dp     // aktif → bar panjang
                                            else
                                                8.dp      // tidak aktif → dot bulat
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (pagerState.currentPage == index)
                                                ModalKitaColors.Green300
                                            else
                                                ModalKitaColors.White50.copy(alpha = 0.4f)
                                        )
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // === SKIP (LEFT) — NEXT (RIGHT) ===
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // Skip → langsung ke next screen
                            Text(
                                text = "Skip",
                                style = typography.bodyMedium,
                                color = ModalKitaColors.White50,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable { onNext() }
                            )

                            // Next / Finish
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage == pages.lastIndex) {
                                        onNext()
                                    } else {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = "Next",
                                    tint = ModalKitaColors.Green300,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
