package net.tevp.dragon_go_notifier

import android.util.Log
import net.tevp.dragon_go_notifier.contentProvider.dao.Game
import org.apache.commons.io.IOUtils
import org.joda.time.Period
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Matchers.eq
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import java.io.InputStream
import java.net.URL
import java.util.*


@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(Log::class)
class GameDatePeriod(val fInput: String, val fExpected: Period) {
    companion object {
        @Parameters
        @JvmStatic
        fun data(): List<Array<Any>> {
            return listOf(
                    arrayOf<Any>("F: 30d (+ 1d)", Period(0, 0, 0, 30, 0, 0, 0, 0)),
                    arrayOf<Any>("F: 29d 1h (+ 1d)", Period(0, 0, 0, 29, 1, 0, 0, 0)),
                    arrayOf<Any>("F: 28d 8h (+ 1d)", Period(0, 0, 0, 28, 8, 0, 0, 0))
            )
        }
    }

    @Test
    fun periodFromDate() {
        PowerMockito.mockStatic(Log::class.java)
        assertEquals(fExpected, DragonServer.periodFromDate(fInput))
    }
}

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(Log::class)
class HolidayDatePeriod(val fInput: String, val fExpected: Period) {
    companion object {
        @Parameters
        @JvmStatic
        fun data(): List<Array<Any>> {
            return listOf(
                    arrayOf<Any>("7h", Period(0, 0, 0, 0, 7, 0, 0, 0)),
                    arrayOf<Any>("1d 7h", Period(0, 0, 0, 1, 7, 0, 0, 0)),
                    arrayOf<Any>("", Period(0, 0, 0, 0, 0, 0, 0, 0))
            )
        }
    }

    @Test
    fun periodFromDate() {
        PowerMockito.mockStatic(Log::class.java)
        assertEquals(fExpected, DragonServer.holidayPeriodFromDate(fInput))
    }
}

@RunWith(PowerMockRunner::class)
@PrepareForTest(IOUtils::class, Log::class)
class GetGames {
    private fun setupGames(vararg resource: String) {
        PowerMockito.mockStatic(Log::class.java)
        val url = PowerMockito.mock(URL::class.java)
        PowerMockito.whenNew(URL::class.java).withParameterTypes(String::class.java)
                .withArguments(Mockito.anyString()).thenReturn(url)

        val resourceDatas = resource.map { s -> IOUtils.toString(ClassLoader.getSystemResourceAsStream(s)) }
        PowerMockito.mockStatic(IOUtils::class.java)
        var mock = Mockito.`when`(IOUtils.toString(Mockito.any(InputStream::class.java), eq("UTF-8")))
        for(data in resourceDatas) {
            mock = mock.thenReturn(data)
        }
    }

    private fun testGames(): Vector<Game> {
        return Vector<Game>(mutableListOf(
                Game(1300500, "multiple-pages", "aendean", Date(2020, 1, 27, 4, 19, 24), true),
                Game(1301036, "multiple-pages", "jposio", Date(2020, 1, 27, 4, 19, 4), true)
        ))
    }

    @Test
    fun getSinglePageGames() {
        setupGames("games-one-page.json")
        assertEquals(testGames(), DragonServer.getGames("multiple-pages", ""))
    }

    @Test
    fun getMultiplePageGames() {
        val games = testGames() + testGames()
        setupGames("games-multiple-1.json", "games-multiple-2.json")
        assertEquals(games, DragonServer.getGames("multiple-pages", ""))
    }
}