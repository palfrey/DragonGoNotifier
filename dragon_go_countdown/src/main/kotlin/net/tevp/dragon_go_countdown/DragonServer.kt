package net.tevp.dragon_go_countdown

import android.util.Log
import android.util.Xml.Encoding.UTF_8
import org.apache.commons.io.IOUtils
import java.io.IOException

object DragonServer {
    @Throws(IOException::class)
    fun Login(username: String, password: String): LoginResult {
        val url = new java.net.URL("http://www.dragongoserver.net/login.php?quick_mode=1&userid=$username&passwd=$password")
        val conn = (HttpURLConnection) url.openConnection()
        val in = new BufferedInputStream(conn.getInputStream())
        val content = org.apache.commons.io.IOUtils.toString(in, "UTF-8")
        val result = LoginResult()
        Log.d("DragonServer", content)
        if (content.contains("#Error")) {
            if (content.contains("wrong_userid")) {
                result.status = LoginStatus.BAD_USERNAME
            } else if (content.contains("wrong_password")) {
                result.status = LoginStatus.BAD_PASSWORD
            } else {
                result.status = LoginStatus.OTHER_ERROR
            }
        } else if (!content.contains("Ok")) {
            result.status = LoginStatus.OTHER_ERROR
        } else {
            for (cookie in httpClient.cookieStore.cookies) {
                if (cookie.name.contentEquals("cookie_sessioncode")) {
                    result.sessionCode = cookie.value
                    result.expiry = cookie.expiryDate
                    result.status = LoginStatus.SUCCESS
                    break
                }
            }
        }
        return result
    }
}