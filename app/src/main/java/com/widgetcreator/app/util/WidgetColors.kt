package com.widgetcreator.app.util

import android.content.Context
import androidx.core.graphics.toColorInt

/** Палитра доступных цветов для оформления виджетов. */
object WidgetColors {

    private val NAMED = linkedMapOf(
        "Blue" to "#1E88E5",
        "Indigo" to "#3949AB",
        "Purple" to "#8E24AA",
        "Pink" to "#D81B60",
        "Red" to "#E53935",
        "Orange" to "#FB8C00",
        "Yellow" to "#FDD835",
        "Green" to "#43A047",
        "Teal" to "#00ACC1",
        "Gray" to "#607D8B",
        "Graphite" to "#37474F",
        "Black" to "#212121",
        "White" to "#FAFAFA"
    )

    /** Имя -> ARGB int. */
    val palette: LinkedHashMap<String, Int> =
        LinkedHashMap(NAMED.mapValues { (_, hex) -> hex.toColorInt() })

    fun nameFor(color: Int): String =
        NAMED.entries.firstOrNull { (_, hex) -> hex.toColorInt() == color }?.key ?: "Custom"

    fun isLight(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return (0.299 * r + 0.587 * g + 0.114 * b) > 160
    }
}
