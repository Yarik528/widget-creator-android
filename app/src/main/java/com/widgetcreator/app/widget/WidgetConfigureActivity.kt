package com.widgetcreator.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.widgetcreator.app.data.WidgetTemplate
import com.widgetcreator.app.ui.theme.WidgetCreatorTheme
import com.widgetcreator.app.util.WidgetCreatorApp

/**
 * Экран выбора шаблона при добавлении виджета на рабочий стол.
 */
class WidgetConfigureActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Обязательный результат "отменено", если пользователь не выбрал шаблон
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val dao = WidgetCreatorApp.from(this).db.widgetDao()
        val templates by dao.observeTemplates().collectAsState(initial = emptyList())

        setContent {
            WidgetCreatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConfigureScreen(templates = templates, onPick = ::onTemplatePicked)
                }
            }
        }
    }

    private fun onTemplatePicked(template: WidgetTemplate) {
        DynamicWidgetProvider.bindAndRender(this, appWidgetId, template.id)

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}

@Composable
private fun ConfigureScreen(
    templates: List<WidgetTemplate>,
    onPick: (WidgetTemplate) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select a widget", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Choose which of your widgets to place on the home screen.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (templates.isEmpty()) {
            Text("No widgets yet. Create one in the Widget Creator app first.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(templates) { template ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(template.name, style = MaterialTheme.typography.titleMedium)
                            Text(template.type.name, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = { onPick(template) }) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}
