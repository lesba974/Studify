package ca.uqac.studify.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.ui.screens.detail.isTaskActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    private val _showArchived = MutableStateFlow(false)
    val showArchived: StateFlow<Boolean> = _showArchived.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun setRepository(repo: TaskRepository) {
        repository = repo
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            combine(
                repository.allTasks,
                _showArchived
            ) { allTasks, archived ->
                allTasks.filter { task ->
                    if (archived) {
                        !isTaskActive(task)
                    } else {
                        isTaskActive(task)
                    }
                }
            }.collect { filteredTasks ->
                _tasks.value = filteredTasks
            }
        }
    }

    fun setShowArchived(value: Boolean) {
        _showArchived.value = value
    }
}