package pl.stapik.budgeting.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BudgetEntryType {
    @SerialName("expense") EXPENSE,
    @SerialName("income") INCOME,
}

@Serializable
data class BudgetEntry(
    val name: String,
    val categoryId: String,
    val type: BudgetEntryType = BudgetEntryType.EXPENSE,
    val plannedAmount: Double? = null,
    val actualAmount: Double? = null,
)
