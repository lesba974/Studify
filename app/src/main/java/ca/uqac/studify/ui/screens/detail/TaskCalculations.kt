package ca.uqac.studify.ui.screens.detail

import ca.uqac.studify.data.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class NextOccurrence(
    val date: String,
    val timeRemaining: String
)

fun calculateNextOccurrence(task: Task): NextOccurrence? {
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

        return NextOccurrence(formattedDate, timeRemaining)

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

        while (true) {
            nextDate = when (task.periodicity) {
                "Quotidienne" -> nextDate.plusDays(1)
                "Hebdomadaire" -> nextDate.plusWeeks(1)
                "Mensuelle" -> nextDate.plusMonths(1)
                else -> return null
            }

            if (nextDate.isAfter(now)) {
                break
            }

            if (nextDate.isEqual(now) && !taskTime.isBefore(currentTime)) {
                break
            }
        }

        return task.copy(date = nextDate.toString())

    } catch (e: Exception) {
        return null
    }
}

fun isTaskActive(task: Task): Boolean {
    if (task.date.isNullOrBlank()) {
        android.util.Log.d("TASK_ACTIVE", "Task ${task.title}: date blank -> ACTIVE")
        return true
    }

    try {
        val taskDate = LocalDate.parse(task.date)
        val taskTime = LocalTime.parse(task.time)
        val now = LocalDate.now()
        val currentTime = LocalTime.now()

        android.util.Log.d("TASK_ACTIVE", "Task ${task.title}:")
        android.util.Log.d("TASK_ACTIVE", "  Date: $taskDate, Time: $taskTime")
        android.util.Log.d("TASK_ACTIVE", "  Now: $now, Current time: $currentTime")
        android.util.Log.d("TASK_ACTIVE", "  Periodicity: ${task.periodicity}")

        if (task.periodicity == "Une fois") {
            val isActive = when {
                taskDate.isAfter(now) -> {
                    android.util.Log.d("TASK_ACTIVE", "  Result: ACTIVE (date future)")
                    true
                }
                taskDate.isEqual(now) && !taskTime.isBefore(currentTime) -> {
                    android.util.Log.d("TASK_ACTIVE", "  Result: ACTIVE (today, time not passed)")
                    true
                }
                else -> {
                    android.util.Log.d("TASK_ACTIVE", "  Result: ARCHIVED (date/time passed)")
                    false
                }
            }
            return isActive
        }

        android.util.Log.d("TASK_ACTIVE", "  Result: ACTIVE (recurring)")
        return true

    } catch (e: Exception) {
        android.util.Log.e("TASK_ACTIVE", "Error for task ${task.title}: ${e.message}")
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