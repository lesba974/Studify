package ca.uqac.studify.ui.screens.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Course
import ca.uqac.studify.data.repository.AcademicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    private lateinit var repository: AcademicRepository

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    fun setRepository(repo: AcademicRepository) {
        repository = repo
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.allCourses.collect { courseList ->
                _courses.value = courseList
            }
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }
}