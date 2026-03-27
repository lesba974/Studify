package ca.uqac.studify.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.ui.screens.detail.isTaskActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    var showArchived by mutableStateOf(false)
        private set

    fun setRepository(repo: TaskRepository) {
        repository = repo
        loadTasks()
    }

    fun toggleShowArchived(archived: Boolean) {
        showArchived = archived
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.allTasks.collect { taskList ->
                _tasks.value = if (showArchived) {
                    taskList.filter { !isTaskActive(it) }
                } else {
                    taskList.filter { isTaskActive(it) }
                }
            }
        }
    }
}