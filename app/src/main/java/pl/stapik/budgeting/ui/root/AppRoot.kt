package pl.stapik.budgeting.ui.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.config.ApiConfig
import pl.stapik.budgeting.data.config.ApiConfigStorage
import pl.stapik.budgeting.ui.budget.BudgetScreen
import pl.stapik.budgeting.ui.budget.BudgetViewModel
import pl.stapik.budgeting.ui.theme.ThemePreferenceStorage
import pl.stapik.budgeting.ui.theme.colorsFor
import pl.stapik.budgeting.util.AppInfo
import pl.stapik.common.cloud.CloudConfig
import pl.stapik.common.presentation.ConnectController
import pl.stapik.common.theme.ThemeId
import pl.stapik.common.ui.shell.AppDefinition
import pl.stapik.common.ui.shell.SettingsFlow
import pl.stapik.common.ui.theme.StapikTheme

private val appDefinition = AppDefinition(
    appId = "budgeting",
    name = "Stapik Budgeting",
    versionName = AppInfo.VERSION_NAME,
    author = "Sebastian Smoliński",
    websiteUrl = "https://github.com/Stapik-Group/stapik-budgeting-mobile",
)

/**
 * @param newConnectController builds the controller of the connect form, prefilled with the saved
 *   connection; called each time the settings are opened.
 */
@Composable
fun AppRoot(
    configStorage: ApiConfigStorage,
    themeStorage: ThemePreferenceStorage,
    viewModel: BudgetViewModel,
    newConnectController: (CloudConfig?) -> ConnectController,
) {
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Budget) }
    var savedConfig by remember { mutableStateOf<ApiConfig?>(null) }
    var currentTheme by remember { mutableStateOf(ThemeId.CLASSIC) }
    val scope = rememberCoroutineScope()
    val scheme = colorsFor(currentTheme)

    LaunchedEffect(Unit) {
        savedConfig = configStorage.load()
        currentTheme = themeStorage.load()
    }

    StapikTheme(currentTheme) {
        Scaffold(
            floatingActionButton = {
                if (screen == AppScreen.Budget) {
                    FloatingActionButton(onClick = { screen = AppScreen.Settings }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_content_description))
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)) {
                when (screen) {
                    is AppScreen.Budget -> BudgetScreen(
                        viewModel = viewModel,
                        scheme = scheme,
                        onNavigateToConnect = { screen = AppScreen.Settings },
                        modifier = Modifier.fillMaxSize(),
                    )

                    is AppScreen.Settings -> {
                        val connectController = remember {
                            newConnectController(savedConfig?.let { CloudConfig(it.serverUrl, it.apiKey, it.passphrase) })
                        }
                        SettingsFlow(
                            app = appDefinition,
                            themeId = currentTheme,
                            onThemeChange = { theme ->
                                currentTheme = theme
                                scope.launch { themeStorage.save(theme) }
                            },
                            connectController = connectController,
                            connected = savedConfig != null,
                            onConnected = {
                                scope.launch {
                                    savedConfig = configStorage.load()
                                    viewModel.refresh()
                                }
                            },
                            onDisconnected = {
                                savedConfig = null
                                viewModel.refresh()
                            },
                            onClose = { screen = AppScreen.Budget },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
