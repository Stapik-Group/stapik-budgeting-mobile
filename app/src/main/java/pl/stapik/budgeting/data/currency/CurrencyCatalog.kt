package pl.stapik.budgeting.data.currency

/**
 * Mirrors resources/currencies.json from the desktop app. The catalog is
 * small and stable enough to keep inline rather than bundling a JSON asset.
 */
object CurrencyCatalog {
    private val symbols = mapOf(
        "PLN" to "zł",
        "EUR" to "€",
        "USD" to "$",
    )

    fun symbolFor(code: String): String = symbols[code] ?: code
}
