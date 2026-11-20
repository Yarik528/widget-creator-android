package com.widgetcreator.app.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.widgetcreator.app.data.AppDatabase
import com.widgetcreator.app.data.WidgetDao
import com.widgetcreator.app.data.WidgetTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel списка и редактора виджетов.
 */
class WidgetViewModel(
    app: Application,
    private val dao: WidgetDao = AppDatabase.get(app).widgetDao()
) : ViewModel() {

    val templates: StateFlow<List<WidgetTemplate>> =
        dao.observeTemplates().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _editing = MutableStateFlow<WidgetTemplate?>(null)
    val editing: StateFlow<WidgetTemplate?> = _editing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun startEdit(template: WidgetTemplate?) {
        _editing.value = template ?: WidgetTemplate(name = "My Widget")
    }

    fun stopEdit() {
        _editing.value = null
    }

    fun save(template: WidgetTemplate) {
        viewModelScope.launch {
            if (template.id == 0L) {
                dao.insertTemplate(template)
                _message.value = "Widget created"
            } else {
                dao.updateTemplate(template)
                _message.value = "Widget updated"
            }
            _editing.value = null
        }
    }

    fun delete(template: WidgetTemplate) {
        viewModelScope.launch {
            dao.deleteInstancesOfTemplate(template.id)
            dao.deleteTemplate(template)
            _message.value = "Widget deleted"
            _editing.value = null
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

class WidgetViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        WidgetViewModel(app) as T
}
