package com.widgetcreator.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.widgetcreator.app.data.WidgetSize
import com.widgetcreator.app.data.WidgetTemplate
import com.widgetcreator.app.data.WidgetType
import com.widgetcreator.app.util.WidgetColors

/**
 * Экран создания/редактирования шаблона виджета с живым предпросмотром.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetEditorScreen(
    initial: WidgetTemplate,
    onSave: (WidgetTemplate) -> Unit,
    onDelete: (WidgetTemplate) -> Unit,
    onCancel: () -> Unit
) {
    var template by remember { mutableStateOf(initial) }
    var typeMenu by remember { mutableStateOf(false) }
    var sizeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            if (template.id == 0L) "New widget" else "Edit widget",
            style = MaterialTheme.typography.headlineSmall
        )

        // Предпросмотр
        WidgetPreview(template)

        OutlinedTextField(
            value = template.name,
            onValueChange = { template = template.copy(name = it) },
            label = { Text("Widget name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Тип
        Box {
            OutlinedButton(onClick = { typeMenu = true }) {
                Text("Type: ${labelFor(template.type)}")
            }
            DropdownMenu(expanded = typeMenu, onDismissRequest = { typeMenu = false }) {
                WidgetType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(labelFor(type)) },
                        onClick = {
                            template = template.copy(type = type)
                            typeMenu = false
                        }
                    )
                }
            }
        }

        // Текст-подпись / содержимое для текстового виджета
        OutlinedTextField(
            value = template.text,
            onValueChange = { template = template.copy(text = it) },
            label = {
                Text(
                    when (template.type) {
                        WidgetType.TEXT -> "Widget text"
                        WidgetType.COUNTER -> "Caption (optional)"
                        else -> "Custom caption (optional)"
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Размер
        Box {
            OutlinedButton(onClick = { sizeMenu = true }) {
                Text("Size: ${template.size.name.lowercase().replaceFirstChar { it.uppercase() }} (${template.size.minWidth}x${template.size.minHeight})")
            }
            DropdownMenu(expanded = sizeMenu, onDismissRequest = { sizeMenu = false }) {
                WidgetSize.entries.forEach { size ->
                    DropdownMenuItem(
                        text = { Text("${size.name} (${size.minWidth}x${size.minHeight})") },
                        onClick = {
                            template = template.copy(size = size)
                            sizeMenu = false
                        }
                    )
                }
            }
        }

        // Цвета
        ColorPickerRow(
            title = "Background color",
            selected = template.backgroundColor,
            onSelected = { template = template.copy(backgroundColor = it) }
        )
        ColorPickerRow(
            title = "Text color",
            selected = template.textColor,
            onSelected = { template = template.copy(textColor = it) }
        )

        // Переключатели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bold text")
            Switch(
                checked = template.boldText,
                onCheckedChange = { template = template.copy(boldText = it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show refresh button")
            Switch(
                checked = template.showRefreshButton,
                onCheckedChange = { template = template.copy(showRefreshButton = it) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onSave(template) },
                enabled = template.name.isNotBlank()
            ) {
                Text("Save")
            }
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
            if (template.id != 0L) {
                OutlinedButton(onClick = { onDelete(template) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun labelFor(type: WidgetType): String = when (type) {
    WidgetType.TEXT -> "Text"
    WidgetType.CLOCK -> "Clock"
    WidgetType.DATE -> "Date"
    WidgetType.COUNTER -> "Counter"
}

@Composable
private fun ColorPickerRow(
    title: String,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WidgetColors.palette.values.forEach { color ->
                val isSelected = color == selected
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(color), CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            }
                        )
                        .clickable { onSelected(color) }
                )
            }
        }
    }
}
