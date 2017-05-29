package net.tevp.dragon_go_countdown.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DragonSyncService : Service() {
    override fun onCreate() {
        synchronized(sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = DragonSyncAdapter(applicationContext, true)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return sSyncAdapter!!.syncAdapterBinder
    }

    companion object {
        private val sSyncAdapterLock = Any()
        private var sSyncAdapter: DragonSyncAdapter? = null
    }
}