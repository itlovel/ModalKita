package com.example.modalkita.ui.borrower.loan.new

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modalkita.domain.model.LoanPurpose
import com.example.modalkita.domain.model.NewLoanDraft
import com.example.modalkita.domain.model.NewLoanSummary
import com.example.modalkita.domain.usecase.CalculateLoanSummaryUseCase
import com.example.modalkita.domain.usecase.CreateLoanApplicationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NewLoanUiState(
    val amountText: String = "",
    val tenorMonths: Int = 12,
    val purpose: LoanPurpose = LoanPurpose.MODAL_USAHA,
    val description: String = "",
    val supportingDocUrl: String? = null,
    val summary: NewLoanSummary? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

class NewLoanViewModel(
    private val calculateSummary: CalculateLoanSummaryUseCase,
    private val createLoanApplication: CreateLoanApplicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewLoanUiState())
    val uiState: StateFlow<NewLoanUiState> = _uiState

    fun updateAmount(text: String) {
        _uiState.value = _uiState.value.copy(amountText = text)
    }

    fun updateTenor(months: Int) {
        _uiState.value = _uiState.value.copy(tenorMonths = months)
    }

    fun updatePurpose(purpose: LoanPurpose) {
        _uiState.value = _uiState.value.copy(purpose = purpose)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun buildSummary(): Boolean {
        val amount = _uiState.value.amountText.replace(".", "").toLongOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Jumlah pinjaman tidak valid")
            return false
        }

        val draft = NewLoanDraft(
            amount = amount,
            tenorMonths = _uiState.value.tenorMonths,
            purpose = _uiState.value.purpose,
            description = _uiState.value.description,
            supportingDocUrl = _uiState.value.supportingDocUrl
        )

        val summary = calculateSummary(draft)
        _uiState.value = _uiState.value.copy(
            summary = summary,
            errorMessage = null
        )
        return true
    }

    fun submitLoan() {
        val summary = _uiState.value.summary ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            try {
                createLoanApplication(summary.draft, summary)
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccess = true
                )
            } catch (e: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Gagal mengajukan pinjaman"
                )
            }
        }
    }
}
