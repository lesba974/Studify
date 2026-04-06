package ca.uqac.studify.ui.screens.detail

import ca.uqac.studify.data.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TaskCalculations(
    val date: String,
    val timeRemaining: String
)

fun calculateNextOccurrence(task: Task): TaskCalculations? {
    if (task.periodicity == "Une fois" || task.date.isNullOrBlank()) {
        return null
    }

    try {
        val taskDate = LocalDate.parse(task.date)
        val taskTime = LocalTime.parse(task.time)
        val now = LocalDate.now()
        val currentTime = LocalTime.now()

        val isPast = when {
            taskDate.isBefore(now) -> true
            taskDate.isEqual(now) && taskTime.isBefore(currentTime) -> true
            else -> false
        }

        val nextDate = if (isPast) {
            when (task.periodicity) {
                "Quotidienne" -> taskDate.plusDays(1)
                "Hebdomadaire" -> taskDate.plusWeeks(1)
                "Mensuelle" -> taskDate.plusMonths(1)
                else -> taskDate
            }
        } else {
            taskDate
        }

        val formattedDate = formatDateToFrench(nextDate.toString())

        val daysUntil = ChronoUnit.DAYS.between(now, nextDate)
        val timeRemaining = when {
            daysUntil < 0 -> "Passée"
            daysUntil == 0L -> "Aujourd'hui à ${task.time}"
            daysUntil == 1L -> "Demain à ${task.time}"
            daysUntil < 7 -> "Dans $daysUntil jours"
            daysUntil < 30 -> "Dans ${daysUntil / 7} semaines"
            else -> "Dans ${daysUntil / 30} mois"
        }

        return TaskCalculations(formattedDate, timeRemaining)

    } catch (e: Exception) {
        return null
    }
}

fun getUpdatedTaskIfNeeded(task: Task): Task? {
    if (task.periodicity == "Une fois" || task.date.isNullOrBlank()) {
        return null
    }

    try {
        val taskDate = LocalDate.parse(task.date)
        val taskTime = LocalTime.parse(task.time)
        val now = LocalDate.now()
        val currentTime = LocalTime.now()

        val isPast = when {
            taskDate.isBefore(now) -> true
            taskDate.isEqual(now) && taskTime.isBefore(currentTime) -> true
            else -> false
        }

        if (!isPast) {
            return null
        }

        var nextDate = taskDate

        while (nextDate.isBefore(now) || nextDate.isEqual(now)) {
            nextDate = when (task.periodicity) {
                "Quotidienne" -> nextDate.plusDays(1)
                "Hebdomadaire" -> nextDate.plusWeeks(1)
                "Mensuelle" -> nextDate.plusMonths(1)
                else -> return null
            }
        }

        return task.copy(date = nextDate.toString())

    } catch (e: Exception) {
        return null
    }
}

fun isTaskActive(task: Task): Boolean {
    if (task.date.isNullOrBlank()) {
        return true
    }

    try {
        val taskDate = LocalDate.parse(task.date)
        val taskTime = LocalTime.parse(task.time)
        val now = LocalDate.now()
        val currentTime = LocalTime.now()

        if (task.periodicity == "Une fois") {
            return when {
                taskDate.isAfter(now) -> true
                taskDate.isEqual(now) && !taskTime.isBefore(currentTime) -> true
                else -> false
            }
        }

        return true

    } catch (e: Exception) {
        return true
    }
}

fun calculateDuration(startTime: String, endTime: String?): String {
    if (endTime.isNullOrBlank()) return "Non définie"

    try {
        val start = LocalTime.parse(startTime)
        val end = LocalTime.parse(endTime)

        val hours = ChronoUnit.HOURS.between(start, end)
        val minutes = ChronoUnit.MINUTES.between(start.plusHours(hours), end)

        return when {
            hours > 0 && minutes > 0 -> "${hours}h${minutes}min"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}min"
            else -> "0min"
        }
    } catch (e: Exception) {
        return "Non définie"
    }
}

fun formatDateToFrench(isoDate: String): String {
    try {
        val date = LocalDate.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)
        return date.format(formatter).replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        return isoDate
    }
}

fun getTodayISO(): String {
    return LocalDate.now().toString()
}