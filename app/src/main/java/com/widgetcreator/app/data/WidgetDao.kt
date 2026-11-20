package com.widgetcreator.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {

    // --- Шаблоны ---

    @Query("SELECT * FROM widget_templates ORDER BY createdAt DESC")
    fun observeTemplates(): Flow<List<WidgetTemplate>>

    @Query("SELECT * FROM widget_templates WHERE id = :id")
    suspend fun getTemplate(id: Long): WidgetTemplate?

    @Insert
    suspend fun insertTemplate(template: WidgetTemplate): Long

    @Update
    suspend fun updateTemplate(template: WidgetTemplate)

    @Delete
    suspend fun deleteTemplate(template: WidgetTemplate)

    @Query("DELETE FROM widget_instances WHERE templateId = :templateId")
    suspend fun deleteInstancesOfTemplate(templateId: Long)

    // --- Экземпляры на экране ---

    @Query("SELECT * FROM widget_instances WHERE appWidgetId = :appWidgetId")
    suspend fun getInstance(appWidgetId: Int): WidgetInstance?

    @Insert
    suspend fun insertInstance(instance: WidgetInstance)

    @Update
    suspend fun updateInstance(instance: WidgetInstance)

    @Query("DELETE FROM widget_instances WHERE appWidgetId = :appWidgetId")
    suspend fun deleteInstance(appWidgetId: Int)

    @Query("SELECT * FROM widget_instances")
    suspend fun getAllInstances(): List<WidgetInstance>
}
