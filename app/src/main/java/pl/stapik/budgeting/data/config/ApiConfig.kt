package pl.stapik.budgeting.data.config

/** [passphrase] is the optional end-to-end passphrase; without it encrypted cloud data cannot be read. */
data class ApiConfig(
    val serverUrl: String,
    val apiKey: String,
    val passphrase: String? = null,
) {
    companion object {
        const val SLOT_KEY = "budget.json"
    }
}
