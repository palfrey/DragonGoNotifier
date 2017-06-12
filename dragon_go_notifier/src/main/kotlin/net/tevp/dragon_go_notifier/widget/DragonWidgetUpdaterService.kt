package net.tevp.dragon_go_notifier.widget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import net.tevp.dragon_go_notifier.contentProvider.DragonItemsContract
import net.tevp.dragon_go_notifier.widget.DragonWidgetUpdaterService.UpdateLock.updateLock
import net.tevp.dragon_go_notifier.widget.DragonWidgetUpdaterService.UpdateLock.updateState
import java.util.concurrent.locks.ReentrantLock

class DragonWidgetUpdaterService : Service() {
    private val TAG = "DragonWidgetUpdater"

    private val binder = Binder()
    private val feedUpdater = FeedUpdater(Handler(), this)
    object UpdateLock {
        var updateState = false
        val updateLock = ReentrantLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateLock.lock()
        try {
            if (updateState) {
                Log.d(TAG, "Already listening for game changes")
            }
            else {
                Log.d(TAG, "Listening for game changes")
                contentResolver.registerContentObserver(DragonItemsContract.Games.CONTENT_URI, true, feedUpdater)
                updateState = true
            }
            return Service.START_NOT_STICKY
        }
        finally {
            updateLock.unlock()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        updateLock.lock()
        try {
            if (updateState) {
                contentResolver.unregisterContentObserver(feedUpdater)
                updateState = false
            }
        }
        finally {
            updateLock.unlock()
        }
    }

    private inner class FeedUpdater(handler: Handler, private val context: Context) : ContentObserver(handler) {
        override fun deliverSelfNotifications(): Boolean {
            return false
        }

        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG, "Got changes to game list")
            val a = AppWidgetManager.getInstance(context) ?: return
            val ids = a.getAppWidgetIds(ComponentName(context, DragonWidgetProvider::class.java))
            if (ids.isEmpty())
                return
            val intent = Intent(context, DragonWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}