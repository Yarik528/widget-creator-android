package com.widgetcreator.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.widgetcreator.app.util.WidgetCreatorApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Универсальный провайдер пользовательских виджетов.
 * Все созданные пользователем виджеты обслуживаются одним провайдером;
 * привязка appWidgetId -> шаблон хранится в Room (таблица widget_instances).
 */
class DynamicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val db = WidgetCreatorApp.from(context).db
        CoroutineScope(Dispatchers.IO).launch {
            ids.forEach { widgetId ->
                val instance = db.widgetDao().getInstance(widgetId) ?: return@forEach
                val template = db.widgetDao().getTemplate(instance.templateId) ?: return@forEach
                val views = WidgetRenderer.render(context, widgetId, template, instance)
                manager.updateAppWidget(widgetId, views)
            }
        }
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        val db = WidgetCreatorApp.from(context).db
        CoroutineScope(Dispatchers.IO).launch {
            ids.forEach { db.widgetDao().deleteInstance(it) }
        }
    }

    companion object {

        /**
         * Привязывает шаблон к новому экземпляру виджета и сразу его рисует.
         * Вызывается из WidgetConfigureActivity при добавлении на рабочий стол.
         */
        fun bindAndRender(context: Context, appWidgetId: Int, templateId: Long) {
            val db = WidgetCreatorApp.from(context).db
            val manager = AppWidgetManager.getInstance(context)

            CoroutineScope(Dispatchers.IO).launch {
                db.widgetDao().deleteInstance(appWidgetId)
                db.widgetDao().insertInstance(
                    com.widgetcreator.app.data.WidgetInstance(
                        appWidgetId = appWidgetId,
                        templateId = templateId
                    )
                )
                val instance = db.widgetDao().getInstance(appWidgetId) ?: return@launch
                val template = db.widgetDao().getTemplate(templateId) ?: return@launch
                val views = WidgetRenderer.render(context, appWidgetId, template, instance)
                manager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
