package com.widgetcreator.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.widgetcreator.app.data.WidgetTemplate

/**
 * Главный экран: список созданных виджетов.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetListScreen(
    templates: List<WidgetTemplate>,
    onEdit: (WidgetTemplate) -> Unit,
    onCreate: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Widget Creator") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) {
                Text("+", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (templates.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(templates, key = { it.id }) { template ->
                        Card(
                            onClick = { onEdit(template) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                Text(
                                    template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                                WidgetPreview(template)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 96.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("No widgets yet", style = MaterialTheme.typography.titleLarge)
        Text(
            "Tap + to create your first widget.\n" +
                "Then long-press the home screen and add it from the widget list.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
