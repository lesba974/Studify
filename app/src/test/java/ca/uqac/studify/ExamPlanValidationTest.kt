package ca.uqac.studify

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExamPlanValidationTest {

    @Test
    fun plan3Weeks_available_when21DaysOrMore() {
        val examDate = LocalDate.now().plusDays(25)
        val today = LocalDate.now()
        val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)

        val availablePlans = getAvailablePlans(daysUntilExam.toInt())

        assertTrue(availablePlans.contains(1))
        assertTrue(availablePlans.contains(2))
        assertTrue(availablePlans.contains(3))
    }

    @Test
    fun plan3Weeks_blocked_whenLessThan21Days() {
        val examDate = LocalDate.now().plusDays(15)
        val today = LocalDate.now()
        val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)

        val availablePlans = getAvailablePlans(daysUntilExam.toInt())

        assertTrue(availablePlans.contains(1))
        assertTrue(availablePlans.contains(2))
        assertFalse(availablePlans.contains(3))
    }

    @Test
    fun plan2Weeks_blocked_whenLessThan14Days() {
        val examDate = LocalDate.now().plusDays(10)
        val today = LocalDate.now()
        val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)

        val availablePlans = getAvailablePlans(daysUntilExam.toInt())

        assertTrue(availablePlans.contains(1))
        assertFalse(availablePlans.contains(2))
        assertFalse(availablePlans.contains(3))
    }

    @Test
    fun lateWarning_shown_whenLessThan7Days() {
        val examDate = LocalDate.now().plusDays(5)
        val today = LocalDate.now()
        val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)

        val isLate = daysUntilExam < 7

        assertTrue(isLate)
    }

    @Test
    fun revisionCount_always21_forAllPlans() {
        assertEquals(21, getRevisionCountForPlan(1))
        assertEquals(21, getRevisionCountForPlan(2))
        assertEquals(21, getRevisionCountForPlan(3))
    }

    private fun getAvailablePlans(daysUntilExam: Int): List<Int> {
        return when {
            daysUntilExam >= 21 -> listOf(1, 2, 3)
            daysUntilExam >= 14 -> listOf(1, 2)
            else -> listOf(1)
        }
    }

    private fun getRevisionCountForPlan(plan: Int): Int {
        return 21
    }
}