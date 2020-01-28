import android.content.Context
import android.content.Intent
import android.os.Build

class DragonUtils {
    companion object {
        @JvmStatic
        fun startService(context: Context, intent: Intent) {
            // https://stackoverflow.com/a/49865024/320546
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }
}