package com.widgetcreator.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.widgetcreator.app.data.WidgetSize
import com.widgetcreator.app.data.WidgetTemplate
import com.widgetcreator.app.data.WidgetType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Живой предпросмотр виджета внутри приложения: рисует то же самое,
 * что и RemoteViews на рабочем столе.
 */
@Composable
fun WidgetPreview(
    template: WidgetTemplate,
    modifier: Modifier = Modifier,
    counterValue: Int = 0
) {
    val height = when (template.size) {
        WidgetSize.SMALL -> 80.dp
        WidgetSize.MEDIUM -> 120.dp
        WidgetSize.LARGE -> 170.dp
    }

    val now = Date()
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
    val date = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now)

    val title: String
    val subtitle: String?

    when (template.type) {
        WidgetType.TEXT -> {
            title = template.text.ifBlank { template.name }
            subtitle = null
        }
        WidgetType.CLOCK -> {
            title = time
            subtitle = template.text.ifBlank { date }
        }
        WidgetType.DATE -> {
            title = date
            subtitle = template.text.ifBlank { time }
        }
        WidgetType.COUNTER -> {
            title = counterValue.toString()
            subtitle = template.text.ifBlank { "Counter" }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Color(template.backgroundColor), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color(template.textColor),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = if (template.boldText) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color(template.textColor).copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
        if (template.showRefreshButton || template.type == WidgetType.COUNTER) {
            val label = if (template.type == WidgetType.COUNTER) "+1" else "Refresh"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        Color(template.textColor).copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(label, color = Color(template.textColor), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
