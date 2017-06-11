package net.tevp.dragon_go_notifier.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DragonAuthenticatorService : Service() {
    override fun onBind(intent: Intent): IBinder {
        val authenticator = DragonAccountAuthenticator(this)
        return authenticator.iBinder
    }
}
