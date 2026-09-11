package pl.stapik.budgeting.ui.budget

import pl.stapik.budgeting.data.model.BudgetPeriod
import java.time.Month
import java.time.format.TextStyle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun monthName(month: Int, style: TextStyle = TextStyle.FULL_STANDALONE): String =
    Month.of(month).getDisplayName(style, Locale.getDefault()).replaceFirstChar { it.uppercase() }

fun periodTitle(period: BudgetPeriod): String = "${monthName(period.month)} ${period.year}"

/** Formats an ISO-8601 instant (e.g. "2026-09-04T19:49:24Z") for display. */
fun formatUpdatedAt(iso: String): String = try {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    formatter.format(Instant.parse(iso))
} catch (_: Exception) {
    iso // fall back to the raw value rather than crash on an unexpected format
}
