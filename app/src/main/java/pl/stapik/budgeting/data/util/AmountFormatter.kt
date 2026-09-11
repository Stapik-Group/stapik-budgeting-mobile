package pl.stapik.budgeting.data.util

import java.util.Locale
import pl.stapik.budgeting.data.currency.CurrencyCatalog

object AmountFormatter {
    /** Same convention as the desktop app: dot for English, comma otherwise. */
    fun format(amount: Double, currencyCode: String): String {
        val decimalSeparator = if (Locale.getDefault().language == "en") '.' else ','
        val text = String.format(Locale.ROOT, "%.2f", amount).replace('.', decimalSeparator)
        return "$text ${CurrencyCatalog.symbolFor(currencyCode)}"
    }

    /** Renders a missing amount as an em dash, matching the desktop app. */
    fun format(amount: Double?, currencyCode: String): String =
        if (amount != null) format(amount, currencyCode) else "—"
}
