package com.example.modalkita.ui.borrower.loan.new

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.modalkita.data.remote.SupabaseClientProvider
import com.example.modalkita.data.repository.BorrowerRepositoryImpl
import com.example.modalkita.domain.usecase.CalculateLoanSummaryUseCase
import com.example.modalkita.domain.usecase.CreateLoanApplicationUseCase

@Composable
fun NewLoanRootAndroid(
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit
) {
    val supabaseClient = remember { SupabaseClientProvider.client }

    val borrowerRepository = remember(supabaseClient) {
        BorrowerRepositoryImpl(
            supabaseClient = supabaseClient
        )
    }

    val calculateSummaryUseCase = remember { CalculateLoanSummaryUseCase() }

    val createLoanApplicationUseCase = remember(borrowerRepository) {
        CreateLoanApplicationUseCase(
            borrowerRepository = borrowerRepository
        )
    }

    val viewModel = remember(calculateSummaryUseCase, createLoanApplicationUseCase) {
        NewLoanViewModel(
            calculateSummary = calculateSummaryUseCase,
            createLoanApplication = createLoanApplicationUseCase
        )
    }

    var step by remember { mutableStateOf(1) }

    when (step) {
        1 -> AndroidLoanDocumentPicker(viewModel = viewModel) { onPickClick, selectedFileName, isUploading ->

            NewLoanFormScreen(
                viewModel = viewModel,
                onBack = onBackToHome,
                onNext = { step = 2 },
                modifier = modifier,
                onDocumentClick = onPickClick     // 🔗 ini yang sambung ke picker
            )
        }

        2 -> NewLoanSummaryScreen(
            viewModel = viewModel,
            onBack = { step = 1 },
            onSubmitSuccess = {
                step = 1
                onBackToHome()
            },
            modifier = modifier
        )
    }
}
