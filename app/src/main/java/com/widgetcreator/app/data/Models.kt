package com.widgetcreator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Тип содержимого виджета. */
enum class WidgetType {
    TEXT,       // произвольный текст
    CLOCK,      // часы (текущее время)
    DATE,       // дата
    COUNTER     // счётчик с инкрементом по нажатию
}

/** Размер сетки ячейки домашнего экрана. */
enum class WidgetSize(val minWidth: Int, val minHeight: Int) {
    SMALL(2, 1),
    MEDIUM(4, 2),
    LARGE(4, 3)
}

/**
 * Шаблон виджета, созданный пользователем в приложении.
 * JSON-представление используется RemoteViewsFactory и экраном предпросмотра.
 */
@Entity(tableName = "widget_templates")
data class WidgetTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: WidgetType = WidgetType.TEXT,
    val text: String = "",
    /** Цвет фона, ARGB int. */
    val backgroundColor: Int = 0xFF1E88E5.toInt(),
    /** Цвет текста, ARGB int. */
    val textColor: Int = 0xFFFFFFFF.toInt(),
    /** Жирный шрифт заголовка. */
    val boldText: Boolean = true,
    val size: WidgetSize = WidgetSize.MEDIUM,
    /** Показывать кнопку "Обновить" на виджете. */
    val showRefreshButton: Boolean = true,
    /** Интервал автообновления в минутах (для часов/даты/счётчика). */
    val updateIntervalMinutes: Int = 60,
    val createdAt: Long = System.currentTimeMillis()
)

/** Экземпляр виджета, размещённый на домашнем экране. */
@Entity(tableName = "widget_instances")
data class WidgetInstance(
    @PrimaryKey val appWidgetId: Int,
    val templateId: Long,
    /** Текущее значение для типа COUNTER. */
    val counterValue: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
