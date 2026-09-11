package pl.stapik.budgeting.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.stapik.budgeting.data.config.ApiConfig
import pl.stapik.budgeting.data.config.ApiConfigStorage
import pl.stapik.budgeting.data.repository.BudgetFetchOutcome
import pl.stapik.budgeting.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repository: BudgetRepository,
    private val configStorage: ApiConfigStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BudgetUiState>(BudgetUiState.Loading)
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _yearFilter = MutableStateFlow<Int?>(null)
    val yearFilter: StateFlow<Int?> = _yearFilter.asStateFlow()

    init {
        refresh()
    }

    fun refresh(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = BudgetUiState.Loading
            }

            try {
                val config = configStorage.load()
                if (config == null) {
                    _uiState.value = BudgetUiState.NotConnected
                    return@launch
                }

                applyOutcome(repository.fetchBudget(config))
            } catch (e: Exception) {
                _uiState.value = BudgetUiState.Error(e.toBudgetLoadError())
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    suspend fun refreshWithConfig(config: ApiConfig): BudgetFetchOutcome {
        _uiState.value = BudgetUiState.Loading
        val outcome = repository.fetchBudget(config)
        applyOutcome(outcome)
        return outcome
    }

    private fun applyOutcome(outcome: BudgetFetchOutcome) {
        _uiState.value = when (outcome) {
            is BudgetFetchOutcome.Fresh ->
                BudgetUiState.Success(outcome.categories, outcome.periods, outcome.currencyCode, isStale = false, outcome.updatedAt)
            is BudgetFetchOutcome.Cached ->
                BudgetUiState.Success(outcome.categories, outcome.periods, outcome.currencyCode, isStale = true, outcome.updatedAt)
            is BudgetFetchOutcome.Failure ->
                BudgetUiState.Error(outcome.cause.toBudgetLoadError())
        }
    }

    fun setYearFilter(year: Int?) {
        _yearFilter.value = year
    }
}
