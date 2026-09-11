package pl.stapik.budgeting.data.config

data class ApiConfig(
    val serverUrl: String,
    val apiKey: String,
) {
    companion object {
        const val SLOT_KEY = "budget.json"
    }
}
