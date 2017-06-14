package net.tevp.dragon_go_notifier.widget

import android.accounts.AccountManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SyncRequest
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import net.tevp.dragon_go_notifier.R
import net.tevp.dragon_go_notifier.authentication.DragonAuthenticatorActivity.Companion.ACCOUNT_TYPE
import net.tevp.dragon_go_notifier.contentProvider.DbSchema
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract.AUTHORITY
import net.tevp.dragon_go_notifier.contentProvider.dao.Game
import net.tevp.dragon_go_notifier.contentProvider.dao.Widget
import java.util.*

class DragonWidgetProvider : AppWidgetProvider() {
    private val TAG = "DragonWidgetProvider"
    private val SYNC_CLICKED = "automaticWidgetSyncButtonClick"
    private val USERNAME = "username"

    var updaterBooted = false

    override fun onEnabled(context: Context) {
        context.startService(Intent(context, DragonWidgetUpdaterService::class.java))
        updaterBooted = true
    }

    override fun onDisabled(context: Context) {
        context.stopService(Intent(context, DragonWidgetUpdaterService::class.java))
        updaterBooted = false
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Log.d(TAG, "Old widgets: $oldWidgetIds, New widgets: $newWidgetIds")
        if (context != null && oldWidgetIds != null && newWidgetIds != null) {
            for ((index, oldWidgetId) in oldWidgetIds.withIndex()) {
                val cursor = context.contentResolver.query(DragonItemsContract.Widgets.CONTENT_URI, emptyArray(), "${DbSchema.Widgets.COL_ID} = ?", arrayOf(oldWidgetId.toString()), "")
                while (cursor.moveToNext()) {
                    val widget = Widget.fromCursor(cursor)
                    widget.widget_id = newWidgetIds[index]
                    context.contentResolver.insert(DragonItemsContract.Widgets.CONTENT_URI, widget.contentValues)
                    context.contentResolver.delete(DragonItemsContract.Widgets.CONTENT_URI, "${DbSchema.Widgets.COL_ID} = ?", arrayOf(oldWidgetId.toString()))
                }
                cursor.close()
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && SYNC_CLICKED == intent.action) {
            val username = intent.getStringExtra(USERNAME)
            val account = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE).single { it.name == username }
            val syncRequest = SyncRequest.Builder()
                    .setManual(true)
                    .setExpedited(true)
                    .setSyncAdapter(account, AUTHORITY)
                    .syncOnce()

            // Fix bug in Android Lollipop
            val extras = Bundle()
            syncRequest.setExtras(extras)

            ContentResolver.requestSync(syncRequest.build())
        }
        else
            super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (!updaterBooted) {
            context.startService(Intent(context, DragonWidgetUpdaterService::class.java))
        }
        for (widget_id in appWidgetIds) {
            Log.d(TAG, "Widget update $widget_id")
            var username: String? = null
            val widgetCursor = context.contentResolver.query(
                    DragonItemsContract.Widgets.CONTENT_URI, emptyArray(), "${DbSchema.Widgets.COL_ID} = ?", arrayOf(widget_id.toString()), "")
            while (widgetCursor.moveToNext()) {
                val widget = Widget.fromCursor(widgetCursor)
                username = widget.username
                Log.d(TAG, "Loading $username for $widget_id")
            }
            widgetCursor.close()
            if (username == null) {
                val settings = appWidgetManager.getAppWidgetOptions(widget_id)
                username = settings.getString(DragonWidgetContract.USERNAME)
                if (username == null) {
                    Log.w(TAG, "No username set for $widget_id")
                    continue
                }
                Log.d(TAG, "Saving $username for $widget_id")
                context.contentResolver.insert(DragonItemsContract.Widgets.CONTENT_URI, Widget(widget_id, username).contentValues)
            }
            Log.d(TAG, "Widget $widget_id is for $username")

            val gameCursor = context.contentResolver.query(
                    DragonItemsContract.Games.CONTENT_URI, emptyArray(), "${DbSchema.Games.COL_USERNAME} = ?", arrayOf(username), "")
            var end_time = Date()
            var games = 0
            var my_turn_games = 0
            while (gameCursor.moveToNext()) {
                val game = Game.fromCursor(gameCursor)
                games++
                if (game.my_turn) {
                    my_turn_games++
                    if (game.end_time > end_time)
                        end_time = game.end_time
                }
            }
            gameCursor.close()
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
                setTextViewText(R.id.allMoves, "$my_turn_games ($games)")
            }
            val backResource = if (days > 0)
                R.drawable.widget_back_amber
            else if (hours < 0)
                R.drawable.widget_back_green
            else
                R.drawable.widget_back_red

            views.setInt(R.id.widgetBackground, "setBackgroundResource", backResource)

            val intent = Intent(context, javaClass)
            intent.action = SYNC_CLICKED
            intent.putExtra(USERNAME, username)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)

            appWidgetManager.updateAppWidget(widget_id, views)
        }
    }

}
