package pl.stapik.budgeting.ui.root

sealed interface AppScreen {
    data object Budget : AppScreen
    data object Connect : AppScreen
    data object Theme : AppScreen
    data object About : AppScreen
}
