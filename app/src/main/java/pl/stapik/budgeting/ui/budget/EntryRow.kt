package pl.stapik.budgeting.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.budgeting.R
import pl.stapik.budgeting.data.model.BudgetEntry
import pl.stapik.budgeting.data.model.BudgetEntryType
import pl.stapik.budgeting.data.model.Category
import pl.stapik.budgeting.data.model.CategoryColor
import pl.stapik.budgeting.data.util.AmountFormatter
import pl.stapik.budgeting.ui.theme.RetroColorScheme
import pl.stapik.budgeting.ui.theme.retroPanel

@Composable
fun EntryRow(
    entry: BudgetEntry,
    category: Category?,
    currencyCode: String,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .retroPanel(scheme)
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background((category?.color ?: CategoryColor.DEFAULT).swatchColor(scheme)),
        )

        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(entry.name, color = scheme.textDark, fontWeight = FontWeight.Bold)
            Text(
                text = category?.name ?: stringResource(R.string.budget_uncategorized),
                color = scheme.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val sign = if (entry.type == BudgetEntryType.INCOME) "+" else "−"
            Text(
                text = "$sign ${AmountFormatter.format(entry.actualAmount, currencyCode)}",
                color = scheme.textDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.budget_planned_short, AmountFormatter.format(entry.plannedAmount, currencyCode)),
                color = scheme.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
