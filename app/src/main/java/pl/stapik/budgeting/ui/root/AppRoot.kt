package pl.stapik.budgeting.ui.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.config.ApiConfig
import pl.stapik.budgeting.data.config.ApiConfigStorage
import pl.stapik.budgeting.data.config.ApiSchemaGuard
import pl.stapik.budgeting.data.repository.BudgetFetchOutcome
import pl.stapik.budgeting.ui.about.AboutScreen
import pl.stapik.budgeting.ui.budget.BudgetLoadError
import pl.stapik.budgeting.ui.budget.BudgetScreen
import pl.stapik.budgeting.ui.budget.BudgetViewModel
import pl.stapik.budgeting.ui.budget.toBudgetLoadError
import pl.stapik.budgeting.ui.connect.ConnectScreen
import pl.stapik.budgeting.ui.connect.ConnectStatus
import pl.stapik.budgeting.ui.theme.AppTheme
import pl.stapik.budgeting.ui.theme.ThemePreferenceStorage
import pl.stapik.budgeting.ui.theme.ThemeScreen
import pl.stapik.budgeting.ui.theme.colorsFor
import pl.stapik.budgeting.ui.theme.toMaterialColorScheme

@Composable
fun AppRoot(
    configStorage: ApiConfigStorage,
    themeStorage: ThemePreferenceStorage,
    viewModel: BudgetViewModel,
) {
    val context = LocalContext.current
    val apiSchemaGuard = remember { ApiSchemaGuard(context.applicationContext, configStorage) }
    var schemaChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        apiSchemaGuard.ensureCurrentSchema()
        schemaChecked = true
    }

    if (!schemaChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Budget) }
    var savedConfig by remember { mutableStateOf<ApiConfig?>(null) }
    var connectStatus by remember { mutableStateOf<ConnectStatus>(ConnectStatus.Idle) }
    var currentTheme by remember { mutableStateOf(AppTheme.CLASSIC) }
    var menuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scheme = colorsFor(currentTheme)

    LaunchedEffect(Unit) {
        savedConfig = configStorage.load()
        currentTheme = themeStorage.load()
        if (savedConfig == null) screen = AppScreen.Connect
    }

    val canGoBackToBudget = screen != AppScreen.Budget && !(screen is AppScreen.Connect && savedConfig == null)
    BackHandler(enabled = canGoBackToBudget) { screen = AppScreen.Budget }

    MaterialTheme(colorScheme = scheme.toMaterialColorScheme()) {
        Scaffold(
            floatingActionButton = {
                Box {
                    FloatingActionButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_content_description))
                    }

                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_connect)) },
                            onClick = { menuExpanded = false; screen = AppScreen.Connect },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings_theme_label)) },
                            onClick = { menuExpanded = false; screen = AppScreen.Theme },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_about)) },
                            onClick = { menuExpanded = false; screen = AppScreen.About },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (screen) {
                    is AppScreen.Budget -> BudgetScreen(
                        viewModel = viewModel,
                        scheme = scheme,
                        onNavigateToConnect = { screen = AppScreen.Connect },
                        modifier = Modifier.fillMaxSize(),
                    )

                    is AppScreen.Connect -> ConnectScreen(
                        initialConfig = savedConfig,
                        status = connectStatus,
                        scheme = scheme,
                        onBack = if (savedConfig != null) { { screen = AppScreen.Budget } } else null,
                        onSave = { config ->
                            scope.launch {
                                connectStatus = ConnectStatus.Testing
                                when (val outcome = viewModel.refreshWithConfig(config)) {
                                    is BudgetFetchOutcome.Fresh -> {
                                        configStorage.save(config)
                                        savedConfig = config
                                        connectStatus = ConnectStatus.Idle
                                        screen = AppScreen.Budget
                                    }

                                    is BudgetFetchOutcome.Cached -> {
                                        connectStatus = ConnectStatus.Error(BudgetLoadError.CannotVerify)
                                    }

                                    is BudgetFetchOutcome.Failure ->
                                        connectStatus = ConnectStatus.Error(outcome.cause.toBudgetLoadError())
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    is AppScreen.Theme -> ThemeScreen(
                        currentTheme = currentTheme,
                        scheme = scheme,
                        onSelect = { theme ->
                            currentTheme = theme
                            scope.launch { themeStorage.save(theme) }
                        },
                        onBack = { screen = AppScreen.Budget },
                        modifier = Modifier.fillMaxSize(),
                    )

                    is AppScreen.About -> AboutScreen(
                        scheme = scheme,
                        onBack = { screen = AppScreen.Budget },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
