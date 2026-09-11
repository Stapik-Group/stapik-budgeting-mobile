package pl.stapik.budgeting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pl.stapik.budgeting.data.cache.DataStoreBudgetCacheStorage
import pl.stapik.budgeting.data.config.DataStoreApiConfigStorage
import pl.stapik.budgeting.data.repository.BudgetRepository
import pl.stapik.budgeting.ui.budget.BudgetViewModel
import pl.stapik.budgeting.ui.root.AppRoot
import pl.stapik.budgeting.ui.theme.DataStoreThemePreferenceStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val configStorage = DataStoreApiConfigStorage(applicationContext)
        val themeStorage = DataStoreThemePreferenceStorage(applicationContext)
        val cacheStorage = DataStoreBudgetCacheStorage(applicationContext)
        val repository = BudgetRepository(cacheStorage)

        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    @Suppress("UNCHECKED_CAST")
                    return BudgetViewModel(repository, configStorage) as T
                }
            },
        )[BudgetViewModel::class.java]

        setContent {
            AppRoot(
                configStorage = configStorage,
                themeStorage = themeStorage,
                viewModel = viewModel,
            )
        }
    }
}
