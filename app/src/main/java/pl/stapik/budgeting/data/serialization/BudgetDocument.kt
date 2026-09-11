package pl.stapik.budgeting.data.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.stapik.budgeting.data.model.BudgetPeriod
import pl.stapik.budgeting.data.model.Category

@Serializable
private data class BudgetPayload(
    val categories: List<Category> = emptyList(),
    val periods: List<BudgetPeriod> = emptyList(),
    val currency: String = "PLN",
)

@Serializable
private data class BudgetDocumentEnvelope(val lastUpdate: String, val payload: BudgetPayload = BudgetPayload())

data class BudgetDocument(
    val lastUpdate: String,
    val categories: List<Category>,
    val periods: List<BudgetPeriod>,
    val currencyCode: String,
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun parse(content: String): BudgetDocument {
            val envelope = json.decodeFromString<BudgetDocumentEnvelope>(content)
            return BudgetDocument(
                lastUpdate = envelope.lastUpdate,
                categories = envelope.payload.categories,
                periods = envelope.payload.periods,
                currencyCode = envelope.payload.currency,
            )
        }
    }
}
