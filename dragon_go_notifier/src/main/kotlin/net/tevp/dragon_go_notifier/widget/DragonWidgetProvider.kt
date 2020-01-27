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
import net.tevp.dragon_go_notifier.contentProvider.dao.User
import net.tevp.dragon_go_notifier.contentProvider.dao.Widget
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Period

class DragonWidgetProvider : AppWidgetProvider() {
    private val TAG = "DragonWidgetProvider"
    private val SYNC_CLICKED = "automaticWidgetSyncButtonClick"
    private val USERNAME = "username"
    private val NEXT_MOVE = "nextMove"
    private val GAMES_DISPLAY = "gamesDisplay"
    private val WIDGET_ID = "widget_id"

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
        if (context != null && intent != null && SYNC_CLICKED == intent.action) {
            val widget_id = intent.getIntExtra(WIDGET_ID, -1)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = setDisplay(context, R.drawable.widget_back_white, widget_id)
            appWidgetManager.updateAppWidget(widget_id, views)

            val options = appWidgetManager.getAppWidgetOptions(widget_id)
            val username = options.getString(USERNAME)
            try {
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

            } catch (e: NoSuchElementException) {
                Log.e(TAG, "Can't find account for $username. Found ${AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE)}")
            }
        }
        else
            super.onReceive(context, intent)
    }

    fun setDisplay(context: Context, backResource: Int, widget_id: Int): RemoteViews {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val options = appWidgetManager.getAppWidgetOptions(widget_id)
        val views = RemoteViews(context.packageName, R.layout.widget).apply {
            setTextViewText(R.id.nextMove, options.getString(NEXT_MOVE, "unk"))
            setTextViewText(R.id.allMoves, options.getString(GAMES_DISPLAY, "0 (0)"))
        }
        views.setInt(R.id.widgetBackground, "setBackgroundResource", backResource)
        return views
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (!updaterBooted) {
            context.startService(Intent(context, DragonWidgetUpdaterService::class.java))
        }
        for (widget_id in appWidgetIds) {
            Log.d(TAG, "Widget update $widget_id")
            val options = appWidgetManager.getAppWidgetOptions(widget_id)
            var username: String? = null
            val widgetCursor = context.contentResolver.query(
                    DragonItemsContract.Widgets.CONTENT_URI, emptyArray(), "${DbSchema.Widgets.COL_ID} = ?", arrayOf(widget_id.toString()), "")
            while (widgetCursor.moveToNext()) {
                val widget = Widget.fromCursor(widgetCursor)
                username = widget.username
                options.putString(USERNAME, username)
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
            var end_time: DateTime? = null
            var games = 0
            var my_turn_games = 0
            while (gameCursor.moveToNext()) {
                val game = Game.fromCursor(gameCursor)
                games++
                if (game.my_turn) {
                    my_turn_games++
                    if (end_time == null || DateTime(game.end_time) < end_time)
                        end_time = DateTime(game.end_time)
                }
            }
            gameCursor.close()
            val userCursor = context.contentResolver.query(
                    DragonItemsContract.Users.CONTENT_URI, emptyArray(), "${DbSchema.Users.COL_USERNAME} = ?", arrayOf(username), "")
            if (userCursor == null || userCursor.isAfterLast) {
                Log.w(TAG, "No user object for $username")
            }
            else {
                userCursor.moveToFirst()
                val user = User.fromCursor(userCursor)
                if (end_time != null) {
                    Log.d(TAG, "End time: $end_time")
                    end_time = end_time.plus(Hours.hours(user.holiday_hours))
                    Log.d(TAG, "Got $user")
                    Log.d(TAG, "End time: $end_time")
                }
            }

            var diff = if (end_time == null) Period.ZERO else Period(DateTime.now(), end_time)
            val days = diff.toStandardDays().days
            diff = diff.minus(Days.days(days))
            val hours = diff.toStandardHours().hours
            Log.d(TAG, "Hours: $hours, Days: $days")

            val display: String
            if (days > 0) {
                display = "${days}d"
            } else if (hours > 0) {
                display = "${hours}h"
            } else {
                display = "∞"
            }

            val backResource = if (end_time == null)
                R.drawable.widget_back_green
            else if (days > 0)
                R.drawable.widget_back_amber
            else
                R.drawable.widget_back_red

            val gameDisplay = "$my_turn_games ($games)"
            val views = setDisplay(context, backResource, widget_id)

            val intent = Intent(context, javaClass)
            intent.action = SYNC_CLICKED
            intent.putExtra(WIDGET_ID, widget_id)

            options.putString(NEXT_MOVE, display)
            options.putString(GAMES_DISPLAY, gameDisplay)
            appWidgetManager.updateAppWidgetOptions(widget_id, options)

            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)
            appWidgetManager.updateAppWidget(widget_id, views)
        }
    }
}
