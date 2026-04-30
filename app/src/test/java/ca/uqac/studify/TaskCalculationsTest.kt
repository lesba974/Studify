package ca.uqac.studify
import ca.uqac.studify.ui.screens.detail.formatDateToFrench
import ca.uqac.studify.ui.screens.detail.calculateDuration
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskCalculationsTest {

    @Test
    fun duration_twoHours_returns2h() {
        val result = calculateDuration("08:00", "10:00")

        assertEquals("2h", result)
    }

    @Test
    fun duration_thirtyMinutes_returns30min() {
        val result = calculateDuration("08:00", "08:30")

        assertEquals("30min", result)
    }

    @Test
    fun duration_oneHourThirty_returns1h30min() {
        val result = calculateDuration("08:00", "09:30")

        assertEquals("1h30min", result)
    }

    @Test
    fun duration_null_returnsNonDefinie() {
        val result = calculateDuration("08:00", null)

        assertEquals("Non définie", result)
    }
    @Test
    fun frenchDateFormat() {
        val result = formatDateToFrench("2026-05-10")

        assert(result.contains("2026"))
    }
}