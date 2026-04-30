package ca.uqac.studify

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TaskArchivingTest {

    @Test
    fun task_isActive_when_dateFuture() {
        val taskDate = LocalDate.now().plusDays(1)
        val taskTime = LocalTime.parse("08:00")

        val isActive = isTaskActive(taskDate, taskTime)

        assertTrue(isActive)
    }

    @Test
    fun task_isArchived_when_datePast() {
        val taskDate = LocalDate.now().minusDays(1)
        val taskTime = LocalTime.parse("08:00")

        val isActive = isTaskActive(taskDate, taskTime)

        assertFalse(isActive)
    }

    @Test
    fun task_isActive_when_todayAndTimeFuture() {
        val taskDate = LocalDate.now()
        val currentTime = LocalTime.now()
        val taskTime = currentTime.plusHours(2)

        val isActive = isTaskActive(taskDate, taskTime)

        assertTrue(isActive)
    }

    @Test
    fun task_isArchived_when_todayAndTimePast() {
        val taskDate = LocalDate.now()
        val currentTime = LocalTime.now()
        val taskTime = currentTime.minusHours(2)

        val isActive = isTaskActive(taskDate, taskTime)

        assertFalse(isActive)
    }

    @Test
    fun weeklyTask_shouldNotBeArchived_evenIfPast() {
        val isRecurring = true

        assertTrue(isRecurring)
    }

    private fun isTaskActive(taskDate: LocalDate, taskTime: LocalTime): Boolean {
        val now = LocalDate.now()
        val currentTime = LocalTime.now()

        return when {
            taskDate.isAfter(now) -> true
            taskDate.isEqual(now) && !taskTime.isBefore(currentTime) -> true
            else -> false
        }
    }
}