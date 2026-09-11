package pl.stapik.budgeting.data.cache

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.model.Category
import pl.stapik.budgeting.data.serialization.BudgetDocument
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

interface BudgetCacheStorage {
    suspend fun load(): CachedBudget?
    suspend fun save(cached: CachedBudget)
}

data class CachedBudget(
    val categories: List<Category>,
    val periods: List<BudgetPeriod>,
    val currencyCode: String,
    val updatedAt: String,
)

private val Context.budgetCacheDataStore by preferencesDataStore(name = "budget_cache")

class DataStoreBudgetCacheStorage(private val context: Context) : BudgetCacheStorage {

    private object Keys {
        val CATEGORIES = stringPreferencesKey("categories_json")
        val PERIODS = stringPreferencesKey("periods_json")
        val CURRENCY = stringPreferencesKey("currency_code")
        val UPDATED_AT = stringPreferencesKey("updated_at")
    }

    override suspend fun load(): CachedBudget? {
        val prefs = context.budgetCacheDataStore.data.first()
        val categoriesJson = prefs[Keys.CATEGORIES] ?: return null
        val periodsJson = prefs[Keys.PERIODS] ?: return null
        val currency = prefs[Keys.CURRENCY] ?: return null
        val updatedAt = prefs[Keys.UPDATED_AT] ?: return null

        // A corrupted or pre-migration cache should not crash the app,
        // just act as no cache (same contract as PlannerCacheStorage).
        return runCatching {
            val categories = Json.decodeFromString<List<Category>>(categoriesJson)
            val periods = Json.decodeFromString<List<BudgetPeriod>>(periodsJson)
            CachedBudget(categories, periods, currency, updatedAt)
        }.getOrElse { error ->
            Log.w(TAG, "Failed to decode cached budget", error)
            null
        }
    }

    override suspend fun save(cached: CachedBudget) {
        val categoriesJson = Json.encodeToString(cached.categories)
        val periodsJson = Json.encodeToString(cached.periods)
        context.budgetCacheDataStore.edit { prefs ->
            prefs[Keys.CATEGORIES] = categoriesJson
            prefs[Keys.PERIODS] = periodsJson
            prefs[Keys.CURRENCY] = cached.currencyCode
            prefs[Keys.UPDATED_AT] = cached.updatedAt
        }
    }

    private companion object {
        const val TAG = "DataStoreBudgetCacheStorage"
    }
}

fun BudgetDocument.toCached() = CachedBudget(
    categories = categories,
    periods = periods,
    currencyCode = currencyCode,
    updatedAt = lastUpdate,
)
