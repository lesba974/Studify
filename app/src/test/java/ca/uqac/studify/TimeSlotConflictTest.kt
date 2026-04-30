package ca.uqac.studify

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime

class TimeSlotConflictTest {

    @Test
    fun noConflict_when_slotEndsBeforeCourseStarts() {
        val slotStart = LocalTime.parse("09:00")
        val slotEnd = LocalTime.parse("10:00")
        val courseStart = LocalTime.parse("11:00")
        val courseEnd = LocalTime.parse("13:00")

        val hasConflict = checkTimeConflict(slotStart, slotEnd, courseStart, courseEnd)

        assertFalse(hasConflict)
    }

    @Test
    fun noConflict_when_slotStartsAfterCourseEnds() {
        val slotStart = LocalTime.parse("14:00")
        val slotEnd = LocalTime.parse("15:00")
        val courseStart = LocalTime.parse("08:00")
        val courseEnd = LocalTime.parse("11:00")

        val hasConflict = checkTimeConflict(slotStart, slotEnd, courseStart, courseEnd)

        assertFalse(hasConflict)
    }

    @Test
    fun conflict_when_slotOverlapsCourse() {
        val slotStart = LocalTime.parse("09:00")
        val slotEnd = LocalTime.parse("10:00")
        val courseStart = LocalTime.parse("08:00")
        val courseEnd = LocalTime.parse("11:00")

        val hasConflict = checkTimeConflict(slotStart, slotEnd, courseStart, courseEnd)

        assertTrue(hasConflict)
    }

    @Test
    fun conflict_when_slotStartsDuringCourse() {
        val slotStart = LocalTime.parse("09:00")
        val slotEnd = LocalTime.parse("12:00")
        val courseStart = LocalTime.parse("08:00")
        val courseEnd = LocalTime.parse("11:00")

        val hasConflict = checkTimeConflict(slotStart, slotEnd, courseStart, courseEnd)

        assertTrue(hasConflict)
    }

    @Test
    fun noConflict_when_slotEndsExactlyWhenCourseStarts() {
        val slotStart = LocalTime.parse("10:00")
        val slotEnd = LocalTime.parse("11:00")
        val courseStart = LocalTime.parse("11:00")
        val courseEnd = LocalTime.parse("13:00")

        val hasConflict = checkTimeConflict(slotStart, slotEnd, courseStart, courseEnd)

        assertFalse(hasConflict)
    }

    private fun checkTimeConflict(
        slotStart: LocalTime,
        slotEnd: LocalTime,
        courseStart: LocalTime,
        courseEnd: LocalTime
    ): Boolean {
        val noOverlap = slotEnd <= courseStart || slotStart >= courseEnd
        return !noOverlap
    }
}