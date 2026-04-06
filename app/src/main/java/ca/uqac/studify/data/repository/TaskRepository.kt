package ca.uqac.studify.data.repository

import ca.uqac.studify.data.local.TaskDao
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.ui.screens.detail.getUpdatedTaskIfNeeded
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun updateTasksToNextOccurrence() {
        val allTasks = taskDao.getAllTasksList()

        allTasks.forEach { task ->
            val updatedTask = getUpdatedTaskIfNeeded(task)

            if (updatedTask != null) {
                taskDao.updateTask(updatedTask)
            }
        }
    }
}