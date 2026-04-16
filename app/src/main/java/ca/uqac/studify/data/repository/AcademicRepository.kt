package ca.uqac.studify.data.repository

import ca.uqac.studify.data.local.CourseDao
import ca.uqac.studify.data.local.ExamDao
import ca.uqac.studify.data.local.TaskDao
import ca.uqac.studify.data.model.Course
import ca.uqac.studify.data.model.Exam
import ca.uqac.studify.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

class AcademicRepository(
    private val courseDao: CourseDao,
    private val examDao: ExamDao,
    private val taskDao: TaskDao
) {
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun getCourseById(id: Long): Course? = courseDao.getCourseById(id)

    suspend fun insertCourse(course: Course): Long = courseDao.insertCourse(course)

    suspend fun updateCourse(course: Course, oldCourseName: String? = null, revisionTime: String = "18:00") {
        if (oldCourseName != null && oldCourseName != course.name) {
            updateRoutinesForCourseName(oldCourseName, course, revisionTime)

            updateExamForCourseName(oldCourseName, course)
        } else {
            updateRoutinesForCourse(course, revisionTime)
        }

        courseDao.updateCourse(course)
    }

    private suspend fun updateRoutinesForCourseName(oldCourseName: String, newCourse: Course, revisionTime: String) {
        val allTasks = taskDao.getAllTasksList()
        val firstOccurrence = getNextDayOfWeek(LocalDate.parse(newCourse.startDate), newCourse.dayOfWeek)

        allTasks.find { task ->
            task.title == "Cours de $oldCourseName" && task.category == "Cours"
        }?.let { courseTask ->
            val updatedTask = courseTask.copy(
                title = "Cours de ${newCourse.name}",
                time = newCourse.startTime,
                endTime = newCourse.endTime,
                date = firstOccurrence.toString(),
                location = newCourse.location
            )
            taskDao.updateTask(updatedTask)
        }

        allTasks.find { task ->
            task.title == "Révision $oldCourseName" &&
                    task.category == "Révision" &&
                    task.periodicity == "Hebdomadaire"
        }?.let { revisionTask ->
            val updatedTask = revisionTask.copy(
                title = "Révision ${newCourse.name}",
                time = revisionTime,
                endTime = calculateEndTime(revisionTime, 60),
                date = firstOccurrence.toString()
            )
            taskDao.updateTask(updatedTask)
        }
    }

    private suspend fun updateExamForCourseName(oldCourseName: String, newCourse: Course) {
        val allExams = examDao.getAllExamsList()

        val exam = allExams.find { it.courseId == newCourse.id }

        exam?.let { currentExam ->
            val updatedExam = currentExam.copy(courseName = newCourse.name)
            examDao.updateExam(updatedExam)

            val allTasks = taskDao.getAllTasksList()

            allTasks.find { task ->
                task.title == "Examen $oldCourseName" && task.category == "Examen"
            }?.let { examTask ->
                val updatedTask = examTask.copy(title = "Examen ${newCourse.name}")
                taskDao.updateTask(updatedTask)
            }

            allTasks.filter { task ->
                task.title == "Révision $oldCourseName" &&
                        task.category == "Révision" &&
                        task.periodicity == "Une fois"
            }.forEach { revisionTask ->
                val updatedTask = revisionTask.copy(title = "Révision ${newCourse.name}")
                taskDao.updateTask(updatedTask)
            }
        }
    }

    private suspend fun updateRoutinesForCourse(course: Course, revisionTime: String = "18:00") {
        val allTasks = taskDao.getAllTasksList()
        val firstOccurrence = getNextDayOfWeek(LocalDate.parse(course.startDate), course.dayOfWeek)

        allTasks.find { task ->
            task.title == "Cours de ${course.name}" && task.category == "Cours"
        }?.let { courseTask ->
            val updatedTask = courseTask.copy(
                title = "Cours de ${course.name}",
                time = course.startTime,
                endTime = course.endTime,
                date = firstOccurrence.toString(),
                location = course.location
            )
            taskDao.updateTask(updatedTask)
        }

        allTasks.find { task ->
            task.title == "Révision ${course.name}" && task.category == "Révision"
        }?.let { revisionTask ->
            val updatedTask = revisionTask.copy(
                title = "Révision ${course.name}",
                time = revisionTime,
                endTime = calculateEndTime(revisionTime, 60),
                date = firstOccurrence.toString()
            )
            taskDao.updateTask(updatedTask)
        }
    }

    suspend fun deleteCourse(course: Course) {
        deleteRoutinesForCourse(course)

        deleteRoutinesForExamsByCourse(course)

        courseDao.deleteCourse(course)
    }

    private suspend fun deleteRoutinesForExamsByCourse(course: Course) {
        val allExams = examDao.getAllExamsList()
        val courseExams = allExams.filter { it.courseId == course.id }

        courseExams.forEach { exam ->
            deleteRoutinesForExam(exam)
        }
    }

    private suspend fun deleteRoutinesForExam(exam: Exam) {
        val allTasks = taskDao.getAllTasksList()

        val tasksToDelete = allTasks.filter { task ->
            (task.title == "Examen ${exam.courseName}" && task.category == "Examen") ||
                    (task.title == "Révision ${exam.courseName}" &&
                            task.category == "Révision" &&
                            task.periodicity == "Une fois")
        }

        tasksToDelete.forEach { task ->
            taskDao.deleteTaskById(task.id)
        }
    }

    private suspend fun deleteRoutinesForCourse(course: Course) {
        val allTasks = taskDao.getAllTasksList()

        val tasksToDelete = allTasks.filter { task ->
            (task.title == "Cours de ${course.name}" && task.category == "Cours") ||
                    (task.title == "Révision ${course.name}" && task.category == "Révision")
        }

        tasksToDelete.forEach { task ->
            taskDao.deleteTaskById(task.id)
        }
    }

    val allExams: Flow<List<Exam>> = examDao.getAllExams()

    suspend fun getExamById(id: Long): Exam? = examDao.getExamById(id)

    fun getExamsByCourse(courseId: Long): Flow<List<Exam>> = examDao.getExamsByCourse(courseId)

    suspend fun insertExam(exam: Exam): Long = examDao.insertExam(exam)

    suspend fun updateExam(exam: Exam) = examDao.updateExam(exam)

    suspend fun deleteExam(exam: Exam) = examDao.deleteExam(exam)

    suspend fun generateRoutinesFromCourse(course: Course, revisionTime: String = "18:00") {
        val startDate = LocalDate.parse(course.startDate)

        val firstOccurrence = getNextDayOfWeek(startDate, course.dayOfWeek)

        val courseTask = Task(
            title = "Cours de ${course.name}",
            description = "Cours magistral",
            category = "Cours",
            time = course.startTime,
            endTime = course.endTime,
            date = firstOccurrence.toString(),
            location = course.location,
            periodicity = "Hebdomadaire",
            priority = "Moyenne"
        )
        taskDao.insertTask(courseTask)

        val revisionTask = Task(
            title = "Révision ${course.name}",
            description = "Relire les notes du cours et faire les exercices",
            category = "Révision",
            time = revisionTime,
            endTime = calculateEndTime(revisionTime, 60),
            date = firstOccurrence.toString(),
            location = "À la maison",
            periodicity = "Hebdomadaire",
            priority = "Moyenne"
        )
        taskDao.insertTask(revisionTask)
    }

    private suspend fun calculateRevisionTime(course: Course, date: LocalDate): String {
        val allCourses = courseDao.getAllCoursesList()
        val sameDayCourses = allCourses.filter { it.dayOfWeek == course.dayOfWeek }

        if (sameDayCourses.isEmpty()) {
            return "18:00"
        }

        val latestEndTime = sameDayCourses.maxOfOrNull {
            LocalTime.parse(it.endTime)
        } ?: LocalTime.parse("18:00")
        var revisionTime = latestEndTime.plusMinutes(30)

        val maxTime = LocalTime.parse("21:00")
        if (revisionTime.isAfter(maxTime)) {
            revisionTime = latestEndTime.plusMinutes(30)
        }

        return String.format("%02d:%02d", revisionTime.hour, revisionTime.minute)
    }

    suspend fun generateRoutinesFromExam(exam: Exam, examDurationMinutes: Int = 180) {
        val examDate = LocalDate.parse(exam.examDate)

        val examTask = Task(
            title = "Examen ${exam.courseName}",
            description = "Examen final",
            category = "Examen",
            time = exam.examTime,
            endTime = calculateEndTime(exam.examTime, examDurationMinutes),
            date = exam.examDate,
            location = exam.location,
            periodicity = "Une fois",
            priority = "Élevée"
        )
        taskDao.insertTask(examTask)

        val allCourses = courseDao.getAllCoursesList()

        val totalWeeks = exam.revisionWeeks
        val totalDays = totalWeeks * 7

        for (daysBefore in 1..totalDays) {
            val revisionDate = examDate.minusDays(daysBefore.toLong())
            val dayOfWeek = revisionDate.dayOfWeek.value

            val coursesThisDay = allCourses.filter { it.dayOfWeek == dayOfWeek }

            val (sessionsPerDay, duration, priority) = when (totalWeeks) {
                1 -> Triple(3, 90, "Élevée")
                2 -> {
                    when {
                        daysBefore <= 7 -> Triple(2, 90, "Élevée")
                        else -> Triple(1, 60, "Moyenne")
                    }
                }
                3 -> Triple(1, 60, "Moyenne")
                else -> Triple(1, 60, "Moyenne")
            }

            val availableSlots = calculateAvailableTimeSlots(
                coursesThisDay,
                sessionsPerDay,
                duration
            )

            availableSlots.forEachIndexed { index, startTime ->
                val description = when (totalWeeks) {
                    1 -> "Révision intensive J-$daysBefore - Rattrapage accéléré"
                    2 -> if (daysBefore <= 7) "Révision intense J-$daysBefore" else "Révision J-$daysBefore"
                    3 -> "Révision progressive J-$daysBefore"
                    else -> "Révision J-$daysBefore"
                }

                val revisionTask = Task(
                    title = "Révision ${exam.courseName}",
                    description = description,
                    category = "Révision",
                    time = startTime,
                    endTime = calculateEndTime(startTime, duration),
                    date = revisionDate.toString(),
                    location = "Bibliothèque",
                    periodicity = "Une fois",
                    priority = priority
                )
                taskDao.insertTask(revisionTask)
            }
        }
    }

    private fun calculateAvailableTimeSlots(
        coursesThisDay: List<Course>,
        sessionsNeeded: Int,
        sessionDuration: Int
    ): List<String> {
        val slots = mutableListOf<String>()

        val preferredSlots = listOf(
            "09:00", "10:00", "11:00",
            "14:00", "15:00", "16:00",
            "18:00", "19:00", "20:00"
        )

        for (slotTime in preferredSlots) {
            if (slots.size >= sessionsNeeded) break

            val slotStart = LocalTime.parse(slotTime)
            val slotEnd = slotStart.plusMinutes(sessionDuration.toLong())

            val hasConflict = coursesThisDay.any { course ->
                val courseStart = LocalTime.parse(course.startTime)
                val courseEnd = LocalTime.parse(course.endTime)

                !(slotEnd.isBefore(courseStart) || slotEnd == courseStart ||
                        slotStart.isAfter(courseEnd) || slotStart == courseEnd)
            }

            if (!hasConflict) {
                slots.add(slotTime)
            }
        }

        if (slots.size < sessionsNeeded) {
            val lateSlots = listOf("21:00", "22:00")
            for (lateSlot in lateSlots) {
                if (slots.size >= sessionsNeeded) break
                slots.add(lateSlot)
            }
        }

        return slots.take(sessionsNeeded)
    }

    suspend fun getRevisionTaskForCourse(courseName: String): Task? {
        val allTasks = taskDao.getAllTasksList()
        return allTasks.find { task ->
            task.title == "Révision $courseName" && task.category == "Révision"
        }
    }

    suspend fun getExamTask(courseName: String): Task? {
        val allTasks = taskDao.getAllTasksList()
        return allTasks.find { task ->
            task.title == "Examen $courseName" && task.category == "Examen"
        }
    }

    private fun getNextDayOfWeek(fromDate: LocalDate, targetDayOfWeek: Int): LocalDate {
        val javaDayOfWeek = DayOfWeek.of(targetDayOfWeek)

        return if (fromDate.dayOfWeek == javaDayOfWeek) {
            fromDate
        } else {
            fromDate.with(TemporalAdjusters.next(javaDayOfWeek))
        }
    }

    private fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        val (hour, minute) = startTime.split(":").map { it.toInt() }
        val totalMinutes = hour * 60 + minute + durationMinutes
        val endHour = (totalMinutes / 60) % 24
        val endMinute = totalMinutes % 60
        return String.format("%02d:%02d", endHour, endMinute)
    }
}