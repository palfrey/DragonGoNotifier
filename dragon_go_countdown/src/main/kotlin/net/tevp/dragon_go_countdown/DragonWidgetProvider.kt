package net.tevp.dragon_go_countdown

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class DragonWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // initializing widget layout
        val views = RemoteViews(context.packageName, R.layout.widget)
        views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_green)
        appWidgetManager.updateAppWidget(appWidgetIds[0], views)
    }
}
