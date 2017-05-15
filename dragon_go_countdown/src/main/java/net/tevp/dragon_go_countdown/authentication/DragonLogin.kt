package net.tevp.dragon_go_countdown.authentication

import android.util.Log
import android.util.Xml.Encoding.UTF_8
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.IOException

object DragonLogin {
    @Throws(IOException::class)
    fun Login(username: String, password: String): LoginResult {
        val httpClient = DefaultHttpClient()
        val request = HttpGet("http://www.dragongoserver.net/login.php?quick_mode=1&userid=$username&passwd=$password")
        val response = httpClient.execute(request)
        val content = IOUtils.toString(response.entity.content, UTF_8.toString())
        val result = LoginResult()
        Log.d("DragonLogin", content)
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