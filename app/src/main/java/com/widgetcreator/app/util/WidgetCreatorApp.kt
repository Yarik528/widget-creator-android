package com.widgetcreator.app.util

import android.content.Context
import com.widgetcreator.app.data.AppDatabase

class WidgetCreatorApp : android.app.Application() {

    val db: AppDatabase by lazy { AppDatabase.get(this) }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set

        fun from(context: Context): WidgetCreatorApp =
            context.applicationContext as WidgetCreatorApp
    }
}
