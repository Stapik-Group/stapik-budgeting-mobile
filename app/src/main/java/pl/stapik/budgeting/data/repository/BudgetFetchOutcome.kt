package pl.stapik.budgeting.data.repository

import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.model.Category

sealed interface BudgetFetchOutcome {
    data class Fresh(
        val categories: List<Category>,
        val periods: List<BudgetPeriod>,
        val currencyCode: String,
        val updatedAt: String,
    ) : BudgetFetchOutcome

    data class Cached(
        val categories: List<Category>,
        val periods: List<BudgetPeriod>,
        val currencyCode: String,
        val updatedAt: String,
    ) : BudgetFetchOutcome

    data class Failure(val cause: Throwable) : BudgetFetchOutcome
}
