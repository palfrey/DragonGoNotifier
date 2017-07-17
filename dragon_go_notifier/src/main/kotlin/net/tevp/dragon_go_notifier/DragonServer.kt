package net.tevp.dragon_go_notifier

import android.util.Log
import net.tevp.dragon_go_notifier.authentication.LoginResult
import net.tevp.dragon_go_notifier.authentication.LoginStatus
import net.tevp.dragon_go_notifier.authentication.NotLoggedInException
import net.tevp.dragon_go_notifier.contentProvider.dao.Game
import org.apache.commons.io.IOUtils.toString
import org.joda.time.DateTime
import org.joda.time.MutablePeriod
import org.joda.time.Period
import org.json.JSONObject
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

    data class Player(var handle: String, var remtime: String)

    fun getGames(accountName: String, authToken: String): List<Game> {
        val TAG = "DragonServer::getGames"
        val url = URL("http://www.dragongoserver.net/quick_do.php?obj=game&cmd=list&view=running&limit=all&with=user_id&lstyle=json")
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("Cookie", "cookie_handle=$accountName; cookie_sessioncode=$authToken")
        Log.d(TAG, "Cookie: ${conn.getRequestProperty("Cookie")}")
        val inStream = BufferedInputStream(conn.inputStream)
        val content = toString(inStream, "UTF-8")
        Log.d(TAG, "Content: $content")
        val items = Vector<Game>()
        val jsonObject = JSONObject(content)
        if (jsonObject.has("error")) {
            val error = jsonObject.getString("error")
            if (error != "") {
                if (error == "not_logged_in") {
                    Log.w(TAG, "Invalid auth token")
                    throw NotLoggedInException()
                }
                else
                    throw Exception("Error from Dragon Go server: $error")
            }
        }
        val records = jsonObject.getJSONArray("list_result")
        //Log.d(TAG, records.toString(2))
        for (i in 0 until records.length()) {
            //Log.d(TAG, "Index: $i")
            val record = records.getJSONObject(i)
            //Log.d(TAG, record.toString(2))
            val white = Player(record.getJSONObject("white_user").getString("handle"), record.getJSONObject("white_gameinfo").getString("remtime"))
            val black = Player(record.getJSONObject("black_user").getString("handle"), record.getJSONObject("black_gameinfo").getString("remtime"))
            val player = if (record.getString("move_color") == "W") white else black
            val opponent = if (white.handle == accountName) black else white
            val end_time: Date? by lazy {
                val period = periodFromDate(player.remtime)
                if (period == null)
                    return@lazy null
                else {
                    DateTime().plus(period).toDate()
                }
            }
            if (end_time != null) {
                val game = Game(record.getInt("id"), accountName, opponent.handle, end_time, player != opponent)
                items.add(game)
            }
        }
        Log.d(TAG, "Games: $items")
        return items
    }

    fun periodFromDate(raw_time: String): Period? {
        val TAG = "DragonServer::periodFro"
        val regex = Regex(".+: (.+) \\(")
        val items = regex.find(raw_time)
        if (items == null)
        {
            Log.w(TAG, "Bad time format: $raw_time")
            return null
        }
        val period = periodFromOnlyDate(items.groups[1]?.value ?: "")
        Log.d(TAG, "Period for $raw_time is $period")
        return period?.toPeriod()
    }

    fun holidayPeriodFromDate(raw_time: String): Period {
        val TAG = "DragonServer::periodFro"
        val period = periodFromOnlyDate(raw_time)
        Log.d(TAG, "Period for $raw_time is $period")
        if (period == null)
            return Period.ZERO
        return period.toPeriod()
    }

    fun periodFromOnlyDate(raw_time: String): Period? {
        val TAG = "DragonServer::onlyDate"
        val period = MutablePeriod()
        for(bit in raw_time.split(" ")) {
            if (bit.isEmpty()) {
                continue
            }

            val kind = bit.last()
            val count = bit.take(bit.length - 1).toInt()
            if (kind == 'd') {
                period.addDays(count)
            } else if (kind == 'h') {
                period.addHours(count)
            } else {
                Log.w(TAG, "Bad time segment: '$bit' in '$raw_time")
                return null
            }
        }
        Log.d(TAG, "Period for $raw_time is $period")
        return period.toPeriod()
    }
}