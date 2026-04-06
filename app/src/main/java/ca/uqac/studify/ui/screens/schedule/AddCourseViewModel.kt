package ca.uqac.studify.ui.screens.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Course
import ca.uqac.studify.data.repository.AcademicRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddCourseViewModel : ViewModel() {

    private lateinit var repository: AcademicRepository

    var name = mutableStateOf("")
    var selectedDay = mutableStateOf(1)
    var startTime = mutableStateOf("08:00")
    var endTime = mutableStateOf("11:00")
    var location = mutableStateOf("")
    var revisionTime = mutableStateOf("18:00")

    private var courseId: Long? = null
    private var oldCourseName: String? = null

    fun setRepository(repo: AcademicRepository) {
        repository = repo
    }

    fun loadCourse(id: Long) {
        courseId = id
        viewModelScope.launch {
            repository.getCourseById(id)?.let { course ->
                name.value = course.name
                oldCourseName = course.name
                selectedDay.value = course.dayOfWeek
                startTime.value = course.startTime
                endTime.value = course.endTime
                location.value = course.location

                val revisionTask = repository.getRevisionTaskForCourse(course.name)
                if (revisionTask != null) {
                    revisionTime.value = revisionTask.time
                }
            }
        }
    }

    suspend fun saveCourse(): Boolean {
        if (name.value.isBlank()) return false

        val course = Course(
            id = courseId ?: 0,
            name = name.value,
            dayOfWeek = selectedDay.value,
            startTime = startTime.value,
            endTime = endTime.value,
            location = location.value,
            startDate = LocalDate.now().toString()
        )

        val id = if (courseId == null) {
            repository.insertCourse(course)
        } else {
            repository.updateCourse(course, oldCourseName, revisionTime.value)
            courseId!!
        }

        if (courseId == null) {
            repository.generateRoutinesFromCourse(course.copy(id = id), revisionTime.value)
        }

        return true
    }

    fun resetForm() {
        name.value = ""
        selectedDay.value = 1
        startTime.value = "08:00"
        endTime.value = "11:00"
        location.value = ""
        revisionTime.value = "18:00"
        courseId = null
        oldCourseName = null
    }

    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Lundi"
            2 -> "Mardi"
            3 -> "Mercredi"
            4 -> "Jeudi"
            5 -> "Vendredi"
            6 -> "Samedi"
            7 -> "Dimanche"
            else -> ""
        }
    }
}