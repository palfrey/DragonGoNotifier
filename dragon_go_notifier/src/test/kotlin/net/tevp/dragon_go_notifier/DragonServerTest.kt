package net.tevp.dragon_go_notifier

import android.util.Log
import org.joda.time.Period
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(Log::class)
class DragonServerTest(val fInput: String, val fExpected: Period) {
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