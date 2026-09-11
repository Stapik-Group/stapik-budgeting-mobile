package pl.stapik.budgeting.ui.budget

import androidx.compose.ui.graphics.Color
import pl.stapik.budgeting.data.model.CategoryColor
import pl.stapik.budgeting.ui.theme.RetroColorScheme

/**
 * Fixed swatch colors matching the desktop app's .category-color-* CSS classes
 * (Classic theme values). Unlike the UI theme, category colors are data-driven
 * and stay the same across all three app themes.
 */
fun CategoryColor.swatchColor(scheme: RetroColorScheme): Color = when (this) {
    CategoryColor.DEFAULT -> scheme.textMuted
    CategoryColor.RED -> Color(0xFF800000)
    CategoryColor.GREEN -> Color(0xFF008000)
    CategoryColor.BLUE -> Color(0xFF000080)
    CategoryColor.YELLOW -> Color(0xFF808000)
    CategoryColor.PURPLE -> Color(0xFF800080)
    CategoryColor.ORANGE -> Color(0xFFFF8000)
    CategoryColor.BROWN -> Color(0xFF804000)
    CategoryColor.PINK -> Color(0xFFC080A0)
    CategoryColor.TEAL -> Color(0xFF008080)
}

/** Text color that stays legible on top of the swatch, matching the desktop palette. */
fun CategoryColor.onSwatchColor(): Color = when (this) {
    CategoryColor.YELLOW, CategoryColor.ORANGE, CategoryColor.PINK -> Color(0xFF000000)
    else -> Color(0xFFFFFFFF)
}
