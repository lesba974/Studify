package ca.uqac.studify.ui.screens.exam

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Course
import ca.uqac.studify.data.model.Exam
import ca.uqac.studify.data.repository.AcademicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AddExamViewModel : ViewModel() {

    private lateinit var repository: AcademicRepository

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    var selectedCourse = mutableStateOf<Course?>(null)
    var examDate = mutableStateOf(LocalDate.now().plusWeeks(2).toString())
    var examTime = mutableStateOf("09:00")
    var location = mutableStateOf("")
    var examDuration = mutableStateOf("180")
    var revisionWeeks = mutableStateOf(2)

    val availableWeeks = derivedStateOf {
        try {
            val examDate = LocalDate.parse(examDate.value)
            val today = LocalDate.now()
            val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)

            when {
                daysUntilExam >= 21 -> listOf(1, 2, 3)
                daysUntilExam >= 14 -> listOf(1, 2)
                else -> listOf(1)
            }
        } catch (e: Exception) {
            listOf(1, 2, 3)
        }
    }

    val isLateWarning = derivedStateOf {
        try {
            val examDate = LocalDate.parse(examDate.value)
            val today = LocalDate.now()
            val daysUntilExam = ChronoUnit.DAYS.between(today, examDate)
            daysUntilExam < 7
        } catch (e: Exception) {
            false
        }
    }

    private var examId: Long? = null

    fun setRepository(repo: AcademicRepository) {
        repository = repo
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.allCourses.collect { courseList ->
                _courses.value = courseList
                if (selectedCourse.value == null && courseList.isNotEmpty()) {
                    selectedCourse.value = courseList.first()
                }
            }
        }
    }

    fun loadExam(id: Long) {
        examId = id
        viewModelScope.launch {
            repository.getExamById(id)?.let { exam ->
                selectedCourse.value = repository.getCourseById(exam.courseId)
                examDate.value = exam.examDate
                examTime.value = exam.examTime
                location.value = exam.location
                revisionWeeks.value = exam.revisionWeeks

                val examTask = repository.getExamTask(exam.courseName)
                if (examTask != null && !examTask.endTime.isNullOrBlank()) {
                    val duration = calculateDurationInMinutes(examTask.time, examTask.endTime)
                    examDuration.value = duration.toString()
                }
            }
        }
    }

    suspend fun saveExam(): Boolean {
        val course = selectedCourse.value ?: return false
        if (location.value.isBlank()) return false

        val exam = Exam(
            id = examId ?: 0,
            courseId = course.id,
            courseName = course.name,
            examDate = examDate.value,
            examTime = examTime.value,
            location = location.value,
            revisionWeeks = revisionWeeks.value
        )

        val id = if (examId == null) {
            repository.insertExam(exam)
        } else {
            repository.updateExam(exam)
            examId!!
        }

        if (examId == null) {
            val durationMinutes = examDuration.value.toIntOrNull() ?: 180
            repository.generateRoutinesFromExam(exam.copy(id = id), durationMinutes)
        }

        return true
    }

    fun resetForm() {
        selectedCourse.value = courses.value.firstOrNull()
        examDate.value = LocalDate.now().plusWeeks(2).toString()
        examTime.value = "09:00"
        location.value = ""
        examDuration.value = "180"
        revisionWeeks.value = 2
        examId = null
    }

    private fun calculateDurationInMinutes(startTime: String, endTime: String): Int {
        try {
            val start = java.time.LocalTime.parse(startTime)
            val end = java.time.LocalTime.parse(endTime)
            return java.time.Duration.between(start, end).toMinutes().toInt()
        } catch (e: Exception) {
            return 180
        }
    }

    fun getRevisionCount(weeks: Int): Int {
        return when (weeks) {
            1 -> 21
            2 -> 21
            3 -> 21
            else -> 0
        }
    }
}