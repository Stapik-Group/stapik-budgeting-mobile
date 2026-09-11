package pl.stapik.budgeting.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.util.AmountFormatter
import pl.stapik.budgeting.ui.theme.RetroButton
import pl.stapik.budgeting.ui.theme.RetroColorScheme
import pl.stapik.budgeting.ui.theme.retroPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodListScreen(
    viewModel: BudgetViewModel,
    scheme: RetroColorScheme,
    onPeriodClick: (BudgetPeriod) -> Unit,
    onNavigateToConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val yearFilter by viewModel.yearFilter.collectAsState()

    when (val state = uiState) {
        is BudgetUiState.Loading -> Box(modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        is BudgetUiState.NotConnected -> CenteredMessageWithAction(
            message = stringResource(R.string.budget_error_not_connected),
            actionLabel = stringResource(R.string.menu_connect),
            onAction = onNavigateToConnect,
            scheme = scheme,
            modifier = modifier,
        )

        is BudgetUiState.Error -> CenteredMessageWithAction(
            message = state.error.toDisplayMessage(),
            actionLabel = stringResource(R.string.retry),
            onAction = { viewModel.refresh() },
            scheme = scheme,
            modifier = modifier,
        )

        is BudgetUiState.Success -> {
            val periods = state.periods.filteredForYear(yearFilter)
            val years = state.periods.availableYears()

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh(isPullToRefresh = true) },
                modifier = modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.isStale) {
                        item { StaleDataBanner(updatedAt = state.updatedAt, scheme = scheme) }
                    }

                    item {
                        YearDropdown(years, yearFilter, viewModel::setYearFilter, scheme)
                    }

                    if (periods.isEmpty()) {
                        item { Text(text = stringResource(R.string.budget_no_periods), color = scheme.textMuted) }
                    }

                    items(periods, key = { "${it.year}-${it.month}" }) { period ->
                        PeriodCard(
                            period = period,
                            currencyCode = state.currencyCode,
                            scheme = scheme,
                            onClick = { onPeriodClick(period) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodCard(
    period: BudgetPeriod,
    currencyCode: String,
    scheme: RetroColorScheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .retroPanel(scheme)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Text(text = periodTitle(period), color = scheme.textDark, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = stringResource(R.string.budget_remaining_label),
                color = scheme.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = " " + AmountFormatter.format(period.actualRemaining, currencyCode),
                color = scheme.textDark,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StaleDataBanner(updatedAt: String, scheme: RetroColorScheme) {
    Text(
        text = stringResource(R.string.budget_stale_banner, formatUpdatedAt(updatedAt)),
        color = scheme.textDark,
        modifier = Modifier
            .fillMaxWidth()
            .background(scheme.borderLight)
            .padding(8.dp),
    )
}

@Composable
private fun YearDropdown(
    years: List<Int>,
    selectedYear: Int?,
    onYearSelected: (Int?) -> Unit,
    scheme: RetroColorScheme,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedYear?.toString() ?: stringResource(R.string.budget_filter_all_years), color = scheme.accent)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.budget_filter_all_years)) },
                onClick = { onYearSelected(null); expanded = false },
            )
            years.forEach { year ->
                DropdownMenuItem(text = { Text(year.toString()) }, onClick = { onYearSelected(year); expanded = false })
            }
        }
    }
}

@Composable
private fun CenteredMessageWithAction(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = scheme.textDark)
            Spacer(modifier = Modifier.height(12.dp))
            RetroButton(text = actionLabel, onClick = onAction, scheme = scheme)
        }
    }
}
