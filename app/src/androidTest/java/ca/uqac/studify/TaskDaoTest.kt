package ca.uqac.studify

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uqac.studify.data.local.TaskDao
import ca.uqac.studify.data.local.StudifyDatabase
import ca.uqac.studify.data.model.Task
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: StudifyDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StudifyDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTask_and_readTask() = runBlocking {

        val task = Task(
            title = "TP Kotlin",
            description = "Finir projet",
            category = "Cours",
            time = "10:00",
            location = "Maison"
        )

        taskDao.insertTask(task)

        val tasks = taskDao.getAllTasksList()

        Assert.assertEquals(1, tasks.size)
        Assert.assertEquals("TP Kotlin", tasks[0].title)
    }

    @Test
    fun deleteTask_removesTask() = runBlocking {

        val task = Task(
            title = "Test",
            description = "Desc",
            category = "Cours",
            time = "09:00",
            location = "UQAC"
        )

        val id = taskDao.insertTask(task)

        taskDao.deleteTaskById(id)

        val tasks = taskDao.getAllTasksList()

        Assert.assertEquals(0, tasks.size)
    }
}