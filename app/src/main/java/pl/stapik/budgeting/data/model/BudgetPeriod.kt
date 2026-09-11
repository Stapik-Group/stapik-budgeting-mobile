package pl.stapik.budgeting.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BudgetPeriod(
    val year: Int,
    val month: Int,
    val startingAmount: Double,
    val entries: List<BudgetEntry> = emptyList(),
) {
    val plannedIncome: Double get() = entries.filter { it.type == BudgetEntryType.INCOME }.sumOf { it.plannedAmount ?: 0.0 }
    val actualIncome: Double get() = entries.filter { it.type == BudgetEntryType.INCOME }.sumOf { it.actualAmount ?: 0.0 }
    val plannedExpense: Double get() = entries.filter { it.type == BudgetEntryType.EXPENSE }.sumOf { it.plannedAmount ?: 0.0 }
    val actualExpense: Double get() = entries.filter { it.type == BudgetEntryType.EXPENSE }.sumOf { it.actualAmount ?: 0.0 }

    val plannedRemaining: Double get() = startingAmount + plannedIncome - plannedExpense
    val actualRemaining: Double get() = startingAmount + actualIncome - actualExpense
}
