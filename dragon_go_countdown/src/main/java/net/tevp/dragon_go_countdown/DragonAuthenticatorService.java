package net.tevp.dragon_go_countdown;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DragonAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        DragonAccountAuthenticator authenticator = new DragonAccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
