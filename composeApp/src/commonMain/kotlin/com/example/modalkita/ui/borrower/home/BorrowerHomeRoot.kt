package com.example.modalkita.ui.borrower.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.modalkita.data.remote.SupabaseClientProvider
import com.example.modalkita.data.repository.BorrowerRepositoryImpl
import com.example.modalkita.domain.usecase.GetBorrowerDashboardUseCase

@Composable
fun BorrowerHomeRoot(
    modifier: Modifier = Modifier,
    onNewApplicationClick: () -> Unit,
    onApplicationDetailClick: () -> Unit
) {
    // 1. Ambil Supabase client dari provider
    val supabaseClient = remember {
        SupabaseClientProvider.client   // kalau di project-mu beda (mis. getClient()), tinggal ganti di sini
    }

    // 2. Buat repository yang pakai Supabase
    val borrowerRepository = remember(supabaseClient) {
        BorrowerRepositoryImpl(
            supabaseClient = supabaseClient   // ✅ nama param disesuaikan dengan constructor
        )
    }

    // 3. Bungkus ke usecase
    val getBorrowerDashboardUseCase = remember(borrowerRepository) {
        GetBorrowerDashboardUseCase(
            borrowerRepository = borrowerRepository
        )
    }

    // 4. ViewModel yang dipakai screen
    val viewModel = remember(getBorrowerDashboardUseCase) {
        BorrowerHomeViewModel(
            getBorrowerDashboardUseCase = getBorrowerDashboardUseCase
        )
    }

    BorrowerHomeScreen(
        viewModel = viewModel,
        onNewApplicationClick = onNewApplicationClick,
        onApplicationDetailClick = onApplicationDetailClick,
        modifier = modifier
    )
}
