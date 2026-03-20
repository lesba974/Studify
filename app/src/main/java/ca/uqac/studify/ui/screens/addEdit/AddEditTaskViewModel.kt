package ca.uqac.studify.ui.screens.addEdit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import kotlinx.coroutines.launch

class AddEditTaskViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var category by mutableStateOf("Travail")
        private set

    var time by mutableStateOf("08:00 AM")
        private set

    var location by mutableStateOf("")
        private set

    var periodicity by mutableStateOf("Une fois")
        private set

    var priority by mutableStateOf("Moyenne")
        private set

    private var currentTaskId: Long? = null

    var endTime by mutableStateOf("")
        private set

    fun setRepository(repo: TaskRepository) {
        repository = repo
    }

    fun updateTitle(newTitle: String) { title = newTitle }
    fun updateDescription(newDescription: String) { description = newDescription }
    fun updateCategory(newCategory: String) { category = newCategory }
    fun updateTime(newTime: String) { time = newTime }
    fun updateLocation(newLocation: String) { location = newLocation }
    fun updatePeriodicity(newPeriodicity: String) { periodicity = newPeriodicity }
    fun updatePriority(newPriority: String) { priority = newPriority }
    fun updateEndTime(newEndTime: String) {
        endTime = newEndTime
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let { task ->
                currentTaskId = task.id
                title = task.title
                description = task.description
                category = task.category
                time = task.time
                location = task.location
                periodicity = task.periodicity
                priority = task.priority
                endTime = task.endTime ?: ""
            }
        }
    }

    fun saveTask(onSuccess: () -> Unit) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val task = Task(
                id = currentTaskId ?: 0,
                title = title,
                description = description,
                category = category,
                time = time,
                endTime = endTime.ifBlank { null },
                location = location,
                periodicity = periodicity,
                priority = priority
            )

            if (currentTaskId == null) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }

            onSuccess()
        }
    }

    fun resetForm() {
        title = ""
        description = ""
        category = "Travail"
        time = "08:00 AM"
        endTime = ""
        location = ""
        periodicity = "Une fois"
        priority = "Moyenne"
        currentTaskId = null
    }
}