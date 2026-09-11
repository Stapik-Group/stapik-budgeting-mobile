package pl.stapik.budgeting.ui.budget

import pl.stapik.budgeting.data.model.BudgetPeriod

fun List<BudgetPeriod>.sortedForDisplay(): List<BudgetPeriod> =
    sortedWith(compareByDescending<BudgetPeriod> { it.year }.thenByDescending { it.month })

fun List<BudgetPeriod>.filteredForYear(year: Int?): List<BudgetPeriod> =
    if (year == null) sortedForDisplay() else filter { it.year == year }.sortedForDisplay()

fun List<BudgetPeriod>.availableYears(): List<Int> =
    map { it.year }.distinct().sortedDescending()
