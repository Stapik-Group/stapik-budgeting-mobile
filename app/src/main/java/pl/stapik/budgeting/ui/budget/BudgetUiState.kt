package pl.stapik.budgeting.ui.budget

import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.model.Category

sealed interface BudgetUiState {
    data object Loading : BudgetUiState
    data object NotConnected : BudgetUiState

    data class Success(
        val categories: List<Category>,
        val periods: List<BudgetPeriod>,
        val currencyCode: String,
        val isStale: Boolean,
        val updatedAt: String,
    ) : BudgetUiState

    data class Error(val error: BudgetLoadError) : BudgetUiState
}
