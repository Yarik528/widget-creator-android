package com.widgetcreator.app.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.RemoteViews
import com.widgetcreator.app.R
import com.widgetcreator.app.data.WidgetInstance
import com.widgetcreator.app.data.WidgetTemplate
import com.widgetcreator.app.data.WidgetType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Рендеринг RemoteViews для шаблона виджета.
 * Используется и на домашнем экране (DynamicWidgetProvider),
 * и в предпросмотре внутри приложения.
 */
object WidgetRenderer {

    const val ACTION_REFRESH = "com.widgetcreator.app.ACTION_REFRESH"

    fun render(
        context: Context,
        appWidgetId: Int,
        template: WidgetTemplate,
        instance: WidgetInstance?
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        views.setInt(R.id.widget_root, "setBackgroundColor", template.backgroundColor)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        val now = Date()

        val title: String
        val subtitle: String?

        when (template.type) {
            WidgetType.TEXT -> {
                title = template.text.ifBlank { template.name }
                subtitle = null
            }
            WidgetType.CLOCK -> {
                title = timeFormat.format(now)
                subtitle = template.text.ifBlank { dateFormat.format(now) }
            }
            WidgetType.DATE -> {
                title = dateFormat.format(now)
                subtitle = template.text.ifBlank { timeFormat.format(now) }
            }
            WidgetType.COUNTER -> {
                title = (instance?.counterValue ?: 0).toString()
                subtitle = template.text.ifBlank { "Counter" }
            }
        }

        views.setTextViewText(R.id.widget_title, title)
        views.setTextColor(R.id.widget_title, template.textColor)
        // RemoteViews.setTypface не поддерживается напрямую: задаём стиль через TextPaint-совместимый int (Typeface.BOLD / NORMAL)
        val style = if (template.boldText) Typeface.BOLD else Typeface.NORMAL
        views.setInt(R.id.widget_title, "setTextStyle", style)

        if (subtitle != null) {
            views.setViewVisibility(R.id.widget_subtitle, android.view.View.VISIBLE)
            views.setTextViewText(R.id.widget_subtitle, subtitle)
            views.setTextColor(R.id.widget_subtitle, fadeColor(template.textColor, 0.7f))
        } else {
            views.setViewVisibility(R.id.widget_subtitle, android.view.View.GONE)
        }

        // Кнопка обновления / инкремента счётчика
        if (template.showRefreshButton || template.type == WidgetType.COUNTER) {
            views.setViewVisibility(R.id.widget_button, android.view.View.VISIBLE)
            val buttonLabel = if (template.type == WidgetType.COUNTER) "+1" else "Refresh"
            views.setTextViewText(R.id.widget_button, buttonLabel)
            views.setTextColor(R.id.widget_button, template.textColor)
            views.setInt(R.id.widget_button, "setBackgroundColor", fadeColor(template.textColor, 0.15f))

            val intent = android.content.Intent(context, WidgetActionReceiver::class.java).apply {
                action = ACTION_REFRESH
                putExtra(WidgetActionReceiver.EXTRA_WIDGET_ID, appWidgetId)
                putExtra(
                    WidgetActionReceiver.EXTRA_KIND,
                    if (template.type == WidgetType.COUNTER)
                        WidgetActionReceiver.KIND_INCREMENT
                    else
                        WidgetActionReceiver.KIND_REFRESH
                )
            }
            val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                android.app.PendingIntent.FLAG_MUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
            else
                android.app.PendingIntent.FLAG_UPDATE_CURRENT
            views.setOnClickPendingIntent(R.id.widget_button, android.app.PendingIntent.getBroadcast(context, appWidgetId, intent, flags))
        } else {
            views.setViewVisibility(R.id.widget_button, android.view.View.GONE)
        }

        return views
    }

    private fun fadeColor(color: Int, factor: Float): Int {
        val r = ((color shr 16) and 0xFF) * factor
        val g = ((color shr 8) and 0xFF) * factor
        val b = (color and 0xFF) * factor
        return Color.rgb(r.toInt().coerceIn(0, 255), g.toInt().coerceIn(0, 255), b.toInt().coerceIn(0, 255))
    }
}
