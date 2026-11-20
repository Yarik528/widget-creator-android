package com.widgetcreator.app.ui

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.widgetcreator.app.ui.theme.WidgetCreatorTheme
import com.widgetcreator.app.widget.WidgetActionReceiver

/**
 * Главное activity: список виджетов + редактор.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WidgetCreatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val factory = remember { WidgetViewModelFactory(context.applicationContext as Application) }
    val viewModel: WidgetViewModel = viewModel(factory = factory)

    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val editing by viewModel.editing.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            // После изменения шаблонов обновляем все виджеты на рабочем столе
            WidgetActionReceiver.updateAll(context)
            viewModel.consumeMessage()
        }
    }

    val current = editing
    if (current == null) {
        WidgetListScreen(
            templates = templates,
            onEdit = { viewModel.startEdit(it) },
            onCreate = { viewModel.startEdit(null) }
        )
    } else {
        WidgetEditorScreen(
            initial = current,
            onSave = { viewModel.save(it) },
            onDelete = { viewModel.delete(it) },
            onCancel = { viewModel.stopEdit() }
        )
    }
}
