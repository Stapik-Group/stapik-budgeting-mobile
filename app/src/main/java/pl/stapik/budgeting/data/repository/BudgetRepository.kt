package pl.stapik.budgeting.data.repository

import pl.stapik.budgeting.data.cache.BudgetCacheStorage
import pl.stapik.budgeting.data.cache.toCached
import pl.stapik.budgeting.data.config.ApiConfig
import pl.stapik.budgeting.data.config.ApiConfig.Companion.SLOT_KEY
import pl.stapik.budgeting.data.serialization.BudgetDocument
import pl.stapik.common.cloud.CloudConfig
import pl.stapik.common.cloud.HttpCloudGateway
import pl.stapik.common.cloud.HttpTransport
import pl.stapik.common.cloud.OkHttpTransport
import pl.stapik.common.model.SlotKey
import pl.stapik.common.sync.CloudGateway

/** The server answered, but it has no budget document in its slot. */
class BudgetNotFoundException : Exception("No budget document on the server")

class BudgetRepository(
    private val cache: BudgetCacheStorage,
    private val transport: HttpTransport = OkHttpTransport(),
) {
    // The gateway keeps the derived end-to-end key, which is slow to compute, so it is reused
    // for as long as the configuration stays the same.
    private var gateway: Pair<ApiConfig, CloudGateway>? = null

    @Synchronized
    private fun gatewayFor(config: ApiConfig): CloudGateway {
        gateway?.let { (known, ready) -> if (known == config) return ready }
        val created = HttpCloudGateway(CloudConfig(config.serverUrl, config.apiKey, config.passphrase), transport)
        gateway = config to created
        return created
    }

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
        val remote = gatewayFor(config).read(SlotKey(SLOT_KEY)) ?: throw BudgetNotFoundException()
        return BudgetDocument.parse(remote.content)
    }
}
