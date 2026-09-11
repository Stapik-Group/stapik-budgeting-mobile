package pl.stapik.budgeting.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val color: CategoryColor = CategoryColor.DEFAULT,
)
