package dev.apslop.apps.pomodoro_timer

import org.junit.Assert.assertEquals
import org.junit.Test

class TimerFormatTest {

    @Test
    fun workDurationIsTwentyFiveMinutes() {
        assertEquals(25L * 60L * 1000L, Phase.WORK.durationMillis)
        assertEquals("25:00", formatMillis(Phase.WORK.durationMillis))
    }

    @Test
    fun breakDurationIsFiveMinutes() {
        assertEquals(5L * 60L * 1000L, Phase.BREAK.durationMillis)
        assertEquals("05:00", formatMillis(Phase.BREAK.durationMillis))
    }

    @Test
    fun formatPadsMinutesAndSeconds() {
        assertEquals("24:59", formatMillis(24L * 60L * 1000L + 59L * 1000L))
        assertEquals("01:00", formatMillis(60L * 1000L))
        assertEquals("00:10", formatMillis(10L * 1000L))
    }

    @Test
    fun formatRoundsUpPartialSecondsAndClampsNegative() {
        assertEquals("00:01", formatMillis(1L))
        assertEquals("00:00", formatMillis(0L))
        assertEquals("00:00", formatMillis(-5_000L))
    }

    @Test
    fun phasesAlternate() {
        assertEquals(Phase.BREAK, Phase.WORK.next)
        assertEquals(Phase.WORK, Phase.BREAK.next)
    }
}
