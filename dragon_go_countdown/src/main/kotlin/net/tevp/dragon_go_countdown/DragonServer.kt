package net.tevp.dragon_go_countdown

import android.util.Log
import net.tevp.dragon_go_countdown.authentication.LoginResult
import net.tevp.dragon_go_countdown.authentication.LoginStatus
import net.tevp.dragon_go_countdown.contentProvider.dao.Game
import org.apache.commons.io.IOUtils.toString
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object DragonServer {
    @Throws(IOException::class)
    fun Login(username: String, password: String): LoginResult {
        val TAG = "DragonServer::Login"
        val url = URL("http://www.dragongoserver.net/login.php?quick_mode=1&userid=$username&passwd=$password")
        Log.d(TAG, "Url: $url")
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        val inStream = BufferedInputStream(conn.inputStream)
        val content = toString(inStream, "UTF-8")
        val result = LoginResult()
        Log.d(TAG, "Content: $content")
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
            val headerFields = conn.headerFields
            val cookiesHeaders = headerFields["Set-Cookie"]
            if (cookiesHeaders != null) {
                for (cookieHeader in cookiesHeaders) {
                    val cookie = HttpCookie.parse(cookieHeader)[0]
                    if (cookie.name.contentEquals("cookie_sessioncode")) {
                        result.sessionCode = cookie.value
                        val expire = Calendar.getInstance()
                        expire.add(Calendar.SECOND, cookie.maxAge.toInt())
                        result.expiry = expire.time
                        result.status = LoginStatus.SUCCESS
                        break
                    }
                }
            }
        }
        Log.d("DragonServer", "Status: ${result.status}")
        return result
    }

    fun getGames(accountName: String, authToken: String): List<Game> {
        val TAG = "DragonServer::getGames"
        var url = URL("http://www.dragongoserver.net/quick_status.php?version=2&order=0")
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("Cookie", "cookie_handle=$accountName; cookie_sessioncode=$authToken")
        Log.d(TAG, "Cookie: ${conn.getRequestProperty("Cookie")}")
        val inStream = BufferedInputStream(conn.inputStream)
        val content = toString(inStream, "UTF-8")
        Log.d(TAG, "Content: $content")
        return emptyList()
    }
}