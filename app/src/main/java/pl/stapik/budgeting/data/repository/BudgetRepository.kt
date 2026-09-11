package pl.stapik.budgeting.data.repository

import pl.stapik.budgeting.data.cache.BudgetCacheStorage
import pl.stapik.budgeting.data.cache.toCached
import pl.stapik.budgeting.data.config.ApiConfig
import pl.stapik.budgeting.data.config.ApiConfig.Companion.SLOT_KEY
import pl.stapik.budgeting.data.network.NetworkModule
import pl.stapik.budgeting.data.serialization.BudgetDocument

class BudgetRepository(
    private val cache: BudgetCacheStorage,
) {
    suspend fun fetchBudget(config: ApiConfig): BudgetFetchOutcome {
        val fresh = runCatching { fetchFromCloud(config) }

        fresh.onSuccess { document -> cache.save(document.toCached()) }

        return fresh.fold(
            onSuccess = { document ->
                BudgetFetchOutcome.Fresh(document.categories, document.periods, document.currencyCode, document.lastUpdate)
            },
            onFailure = { error ->
                val cached = runCatching { cache.load() }.getOrNull()
                if (cached != null) {
                    BudgetFetchOutcome.Cached(cached.categories, cached.periods, cached.currencyCode, cached.updatedAt)
                } else {
                    BudgetFetchOutcome.Failure(error)
                }
            },
        )
    }

    private suspend fun fetchFromCloud(config: ApiConfig): BudgetDocument {
        val api = NetworkModule.createApi(config.serverUrl)
        val response = api.getDocument(slotKey = SLOT_KEY, apiKey = config.apiKey)
        return BudgetDocument.parse(response.content)
    }
}
