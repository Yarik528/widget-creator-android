package com.widgetcreator.app.widget

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import com.widgetcreator.app.util.WidgetCreatorApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Обрабатывает нажатия кнопок на виджетах:
 * - KIND_REFRESH: перерисовать виджет (обновить время/дату);
 * - KIND_INCREMENT: увеличить значение счётчика и перерисовать.
 */
class WidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != WidgetRenderer.ACTION_REFRESH) return

        val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val kind = intent.getStringExtra(EXTRA_KIND) ?: KIND_REFRESH
        val db = WidgetCreatorApp.from(context).db
        val manager = AppWidgetManager.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            val instance = db.widgetDao().getInstance(widgetId)
            val template = instance?.let { db.widgetDao().getTemplate(it.templateId) }
                ?: return@launch

            var updatedInstance = instance
            if (kind == KIND_INCREMENT) {
                updatedInstance = instance.copy(counterValue = instance.counterValue + 1)
                db.widgetDao().updateInstance(updatedInstance)
            }

            val views = WidgetRenderer.render(context, widgetId, template, updatedInstance)
            manager.updateAppWidget(widgetId, views)
        }
    }

    companion object {
        const val EXTRA_WIDGET_ID = "com.widgetcreator.app.extra.WIDGET_ID"
        const val EXTRA_KIND = "com.widgetcreator.app.extra.KIND"
        const val KIND_REFRESH = "refresh"
        const val KIND_INCREMENT = "increment"

        /** Обновляет все экземпляры виджетов приложения на рабочем столе. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, DynamicWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                context.sendBroadcast(
                    Intent(context, DynamicWidgetProvider::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    }
                )
            }
        }
    }
}
