package pl.stapik.budgeting.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors CategoryColor from the desktop app 1:1 (same nine named colors + default). */
@Serializable
enum class CategoryColor {
    @SerialName("default") DEFAULT,
    @SerialName("red") RED,
    @SerialName("green") GREEN,
    @SerialName("blue") BLUE,
    @SerialName("yellow") YELLOW,
    @SerialName("purple") PURPLE,
    @SerialName("orange") ORANGE,
    @SerialName("brown") BROWN,
    @SerialName("pink") PINK,
    @SerialName("teal") TEAL,
}
