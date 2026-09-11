package pl.stapik.budgeting.ui.budget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.ui.theme.RetroColorScheme

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    scheme: RetroColorScheme,
    onNavigateToConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPeriodKey by rememberSaveable { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    val selectedPeriod: BudgetPeriod? = remember(selectedPeriodKey, uiState) {
        val key = selectedPeriodKey ?: return@remember null
        (uiState as? BudgetUiState.Success)?.periods?.firstOrNull { "${it.year}-${it.month}" == key }
    }

    BackHandler(enabled = selectedPeriod != null) { selectedPeriodKey = null }

    if (selectedPeriod != null && uiState is BudgetUiState.Success) {
        val state = uiState as BudgetUiState.Success
        PeriodDetailScreen(
            period = selectedPeriod,
            categories = state.categories,
            currencyCode = state.currencyCode,
            scheme = scheme,
            onBack = { selectedPeriodKey = null },
            modifier = modifier.fillMaxSize(),
        )
    } else {
        PeriodListScreen(
            viewModel = viewModel,
            scheme = scheme,
            onPeriodClick = { period -> selectedPeriodKey = "${period.year}-${period.month}" },
            onNavigateToConnect = onNavigateToConnect,
            modifier = modifier.fillMaxSize(),
        )
    }
}
