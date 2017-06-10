package net.tevp.dragon_go_countdown.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import net.tevp.dragon_go_countdown.R
import net.tevp.dragon_go_countdown.contentProvider.DbSchema
import net.tevp.dragon_go_countdown.contentProvider.DragonItemsContract
import net.tevp.dragon_go_countdown.contentProvider.dao.Game
import java.util.*

class DragonWidgetProvider : AppWidgetProvider() {
    private val TAG = "DragonWidgetProvider"

    override fun onEnabled(context: Context) {
        context.startService(Intent(context, DragonWidgetUpdaterService::class.java))
    }

    override fun onDisabled(context: Context) {
        context.stopService(Intent(context, DragonWidgetUpdaterService::class.java))
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widget in appWidgetIds) {
            Log.d(TAG, "Widget update $widget")
            val settings = appWidgetManager.getAppWidgetOptions(widget)
            val username: String? = settings.getString(DragonWidgetContract.USERNAME)
            if (username == null) {
                Log.w(TAG, "No username set for $widget")
                continue
            }
            Log.d(TAG, "Widget $widget is for $username")

            val cursor = context.contentResolver.query(
                    DragonItemsContract.Games.CONTENT_URI, emptyArray(), "${DbSchema.COL_USERNAME} = ?", arrayOf(username), "")
            var end_time = Date()
            var games = 0
            while (cursor.moveToNext()) {
                val game = Game.fromCursor(cursor)
                games++
                if (game.end_time > end_time) {
                    end_time = game.end_time
                }
            }
            cursor.close()
            Log.d(TAG, end_time.toString())
            val diff = end_time.time - Date().time
            val hours = Math.floor(diff / (60 * 60 * 1000.0)).toInt()
            val days = Math.floor(hours / 24.0).toInt()
            Log.d(TAG, "Hours: $hours, Days: $days")
            val display: String
            if (days > 0) {
                display = "${days}d"
            } else if (hours > 0) {
                display = "${hours}h"
            } else {
                display = "n/a"
            }

            // initializing widget layout
            val views = RemoteViews(context.packageName, R.layout.widget).apply {
                setTextViewText(R.id.nextMove, display)
                setTextViewText(R.id.allMoves, "$games")
            }
            if (days > 0)
                views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_green)
            else
                views.setInt(R.id.widgetBackground, "setBackgroundResource", R.drawable.widget_back_red)
            appWidgetManager.updateAppWidget(widget, views)
        }
    }

}
