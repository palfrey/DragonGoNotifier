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
        val url = URL("http://www.dragongoserver.net/login.php?quick_mode=1&userid=$username&passwd=$password")
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        val inStream = BufferedInputStream(conn.inputStream)
        val content = toString(inStream, "UTF-8")
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
            val headerFields = conn.headerFields
            val COOKIES_HEADER = "Set-Cookie"
            val cookiesHeader = headerFields[COOKIES_HEADER]
            if (cookiesHeader != null) {
                for (cookie in cookiesHeader) {
                    val cookie = HttpCookie.parse(cookie)[0]
                    if (cookie.name.contentEquals("cookie_sessioncode")) {
                        result.sessionCode = cookie.value
                        val expire = Calendar.getInstance()
                        expire.add(Calendar.SECOND, cookie.maxAge as Int)
                        result.expiry = expire.time
                        result.status = LoginStatus.SUCCESS
                        break
                    }
                }
            }
        }
        return result
    }

    fun getGames(authToken: String): List<Game> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}