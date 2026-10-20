package pl.stapik.budgeting.ui.root

sealed interface AppScreen {
    data object Budget : AppScreen
    data object Settings : AppScreen
}
