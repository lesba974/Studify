package ca.uqac.studify.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    fun setRepository(repo: TaskRepository) {
        repository = repo
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val loadedTask = repository.getTaskById(taskId)
            _task.value = loadedTask
        }
    }

    fun deleteTask(taskId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
            onSuccess()
        }
    }

    fun duplicateTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _task.value?.let { currentTask ->
                val duplicatedTask = currentTask.copy(
                    id = 0,
                    title = "${currentTask.title} (Copie)"
                )
                repository.insertTask(duplicatedTask)
                onSuccess()
            }
        }
    }
}