package com.example.modalkita.ui.borrower.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modalkita.domain.model.BorrowerHomeDashboard
import com.example.modalkita.domain.model.CreditCategory
import com.example.modalkita.domain.model.LoanApplicationStatus
import com.example.modalkita.domain.usecase.GetBorrowerDashboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BorrowerHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val dashboard: BorrowerHomeDashboard? = null
)

class BorrowerHomeViewModel(
    private val getBorrowerDashboardUseCase: GetBorrowerDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BorrowerHomeUiState(isLoading = true)
    )
    val uiState: StateFlow<BorrowerHomeUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = BorrowerHomeUiState(isLoading = true)
            try {
                val dashboard = getBorrowerDashboardUseCase()
                _uiState.value = BorrowerHomeUiState(
                    isLoading = false,
                    dashboard = dashboard
                )
            } catch (e: Throwable) {

                // Fallback: tampilkan default dashboard daripada error merah
                val defaultDashboard = BorrowerHomeDashboard(
                    username = "User",
                    creditScore = 1000,
                    creditCategory = CreditCategory.SANGAT_BAIK,
                    canApply = true,
                    activeApplicationStatus = LoanApplicationStatus.NONE,
                    fundedPercentage = 0,
                    investorCount = 0,
                    loanAmount = null,
                    fundedAmount = null
                )

                _uiState.value = BorrowerHomeUiState(
                    isLoading = false,
                    dashboard = defaultDashboard,
                    errorMessage = e.message   // opsional, buat logging
                )
            }
        }
    }
}
