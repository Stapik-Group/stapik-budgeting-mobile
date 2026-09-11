package pl.stapik.budgeting.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.model.BudgetEntryType
import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.model.Category
import pl.stapik.budgeting.data.util.AmountFormatter
import pl.stapik.budgeting.ui.common.ScreenHeader
import pl.stapik.budgeting.ui.theme.RetroColorScheme
import pl.stapik.budgeting.ui.theme.retroPanel

private enum class EntryTypeFilter { ALL, EXPENSE, INCOME }

@Composable
fun PeriodDetailScreen(
    period: BudgetPeriod,
    categories: List<Category>,
    currencyCode: String,
    scheme: RetroColorScheme,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoriesById = remember(categories) { categories.associateBy { it.id } }
    var typeFilter by remember { mutableStateOf(EntryTypeFilter.ALL) }

    val entries = period.entries.filter {
        when (typeFilter) {
            EntryTypeFilter.ALL -> true
            EntryTypeFilter.EXPENSE -> it.type == BudgetEntryType.EXPENSE
            EntryTypeFilter.INCOME -> it.type == BudgetEntryType.INCOME
        }
    }

    Column(modifier = modifier.fillMaxSize().background(scheme.windowBackground)) {
        ScreenHeader(title = periodTitle(period), scheme = scheme, onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { PeriodSummary(period = period, currencyCode = currencyCode, scheme = scheme) }

            item {
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    TypeFilterButton(EntryTypeFilter.ALL, typeFilter, R.string.budget_filter_all_types, scheme) { typeFilter = it }
                    TypeFilterButton(EntryTypeFilter.EXPENSE, typeFilter, R.string.dialog_entry_type_expense, scheme) { typeFilter = it }
                    TypeFilterButton(EntryTypeFilter.INCOME, typeFilter, R.string.dialog_entry_type_income, scheme) { typeFilter = it }
                }
            }

            if (entries.isEmpty()) {
                item { Text(text = stringResource(R.string.budget_no_entries), color = scheme.textMuted) }
            }

            items(entries, key = { it.name + it.categoryId + it.type }) { entry ->
                EntryRow(
                    entry = entry,
                    category = categoriesById[entry.categoryId],
                    currencyCode = currencyCode,
                    scheme = scheme,
                )
            }
        }
    }
}

@Composable
private fun PeriodSummary(period: BudgetPeriod, currencyCode: String, scheme: RetroColorScheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .retroPanel(scheme)
            .padding(16.dp),
    ) {
        SummaryRow(stringResource(R.string.budget_starting_amount), AmountFormatter.format(period.startingAmount, currencyCode), scheme)
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow(
            stringResource(R.string.view_header_planned),
            "${AmountFormatter.format(period.plannedIncome, currencyCode)} / ${AmountFormatter.format(period.plannedExpense, currencyCode)}",
            scheme,
        )
        SummaryRow(
            stringResource(R.string.view_header_actual),
            "${AmountFormatter.format(period.actualIncome, currencyCode)} / ${AmountFormatter.format(period.actualExpense, currencyCode)}",
            scheme,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow(
            stringResource(R.string.budget_remaining_label),
            AmountFormatter.format(period.actualRemaining, currencyCode),
            scheme,
            emphasize = true,
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, scheme: RetroColorScheme, emphasize: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = scheme.textMuted, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            color = scheme.textDark,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun TypeFilterButton(
    value: EntryTypeFilter,
    selected: EntryTypeFilter,
    labelRes: Int,
    scheme: RetroColorScheme,
    onSelect: (EntryTypeFilter) -> Unit,
) {
    TextButton(onClick = { onSelect(value) }) {
        Text(
            stringResource(labelRes),
            color = if (value == selected) scheme.accent else scheme.textMuted,
            fontWeight = if (value == selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
