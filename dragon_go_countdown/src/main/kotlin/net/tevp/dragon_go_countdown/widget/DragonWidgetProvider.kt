package net.tevp.dragon_go_countdown.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import net.tevp.dragon_go_countdown.R
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract
import net.tevp.dragon_go_countdown.contentProvider.dao.Game
import java.util.*

class DragonWidgetProvider : AppWidgetProvider() {
    private val TAG = "DragonWidgetProvider"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "Widget update")
        val cursor = context.contentResolver.query(
                DragonItemsContract.Games.CONTENT_URI, emptyArray(), "", emptyArray(), "")
        var end_time = Date()
        while (cursor.moveToNext()) {
            val game = Game.fromCursor(cursor)
            if (game.end_time > end_time) {
                end_time = game.end_time
            }
        }
        cursor.close()
        Log.d(TAG, end_time.toString())
        val diff = end_time.time - Date().time
        val hours = Math.floor(diff / (60*60*1000.0)).toInt()
        val days = Math.floor(hours / 24.0).toInt()
        Log.d(TAG, "Hours: $hours, Days: $days")
        val display: String
        if (days > 0) {
            display = "${days}d"
        }
        else {
            display = "{$hours}h"
        }

        // initializing widget layout
        val views = RemoteViews(context.packageName, R.layout.widget)
        views.setTextViewText(R.id.nextMove, display)
        if (days > 0)
            views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_green)
        else
            views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_red)
        appWidgetManager.updateAppWidget(appWidgetIds[0], views)
    }
}
