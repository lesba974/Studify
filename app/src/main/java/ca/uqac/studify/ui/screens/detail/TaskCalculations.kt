package ca.uqac.studify.ui.screens.detail

import ca.uqac.studify.data.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TaskCalculations(
    val date: String,
    val timeRemaining: String
)

fun calculateNextOccurrence(task: Task): TaskCalculations? {
    if (task.periodicity == "Une fois") {
        return null
    }

    val now = LocalDate.now()
    val nextDate = when (task.periodicity) {
        "Quotidien" -> now.plusDays(1)
        "Hebdomadaire" -> now.plusWeeks(1)
        "Mensuel" -> now.plusMonths(1)
        else -> return null
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH)
    val formattedDate = nextDate.format(formatter).replaceFirstChar { it.uppercase() }

    val timeStr = when {
        task.time.contains(":") -> {
            val parts = task.time.split(" ")
            parts.firstOrNull { it.contains(":") } ?: task.time
        }
        else -> task.time
    }

    val daysUntil = ChronoUnit.DAYS.between(now, nextDate)
    val timeRemaining = when {
        daysUntil == 0L -> "Aujourd'hui"
        daysUntil == 1L -> "Demain"
        daysUntil < 7 -> "Dans $daysUntil jours"
        daysUntil < 14 -> "Dans 1 semaine"
        daysUntil < 30 -> "Dans ${daysUntil / 7} semaines"
        else -> "Dans ${daysUntil / 30} mois"
    }

    return TaskCalculations(
        date = "$formattedDate à $timeStr",
        timeRemaining = timeRemaining
    )
}

fun calculateDuration(startTime: String, endTime: String?): String {
    if (endTime.isNullOrBlank()) {
        return "Non définie"
    }

    try {
        val startParts = startTime.split(" ").firstOrNull { it.contains(":") }?.split(":") ?: return "Non définie"
        val startHour = startParts[0].toIntOrNull() ?: return "Non définie"
        val startMinute = startParts.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0

        val endParts = endTime.split(" ").firstOrNull { it.contains(":") }?.split(":") ?: return "Non définie"
        val endHour = endParts[0].toIntOrNull() ?: return "Non définie"
        val endMinute = endParts.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0

        val totalMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute)

        if (totalMinutes <= 0) return "Non définie"

        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h${minutes}"
            hours > 0 -> "${hours}h"
            else -> "${minutes}min"
        }
    } catch (e: Exception) {
        return "Non définie"
    }
}