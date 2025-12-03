package com.example.modalkita.ui.borrower.loan.new

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.modalkita.data.remote.SupabaseClientProvider
import com.example.modalkita.data.repository.BorrowerRepositoryImpl
import com.example.modalkita.domain.usecase.CalculateLoanSummaryUseCase
import com.example.modalkita.domain.usecase.CreateLoanApplicationUseCase

@Composable
fun NewLoanRoot(
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit
) {
    // --- Wiring Supabase & repository ---
    val supabaseClient = remember {
        SupabaseClientProvider.client
    }

    val borrowerRepository = remember(supabaseClient) {
        BorrowerRepositoryImpl(
            supabaseClient = supabaseClient
        )
    }

    // --- Use case untuk summary & create loan ---
    val calculateSummaryUseCase = remember {
        CalculateLoanSummaryUseCase()
    }

    val createLoanApplicationUseCase = remember(borrowerRepository) {
        CreateLoanApplicationUseCase(
            borrowerRepository = borrowerRepository
        )
    }

    // --- ViewModel untuk 2 screen sekaligus ---
    val viewModel = remember(calculateSummaryUseCase, createLoanApplicationUseCase) {
        NewLoanViewModel(
            calculateSummary = calculateSummaryUseCase,
            createLoanApplication = createLoanApplicationUseCase
        )
    }

    // --- Step form → summary ---
    var step by remember { mutableStateOf(1) }

    when (step) {
        1 -> NewLoanFormScreen(
            viewModel = viewModel,
            onBack = onBackToHome,       // back dari form → balik ke dashboard
            onNext = { step = 2 },       // lanjut ke summary
            modifier = modifier
        )

        2 -> NewLoanSummaryScreen(
            viewModel = viewModel,
            onBack = { step = 1 },       // back dari summary → balik ke form
            onSubmitSuccess = {
                // setelah submit sukses → reset ke form & balik ke dashboard
                step = 1
                onBackToHome()
            },
            modifier = modifier
        )
    }
}
