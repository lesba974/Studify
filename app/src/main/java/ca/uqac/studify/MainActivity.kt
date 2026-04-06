package ca.uqac.studify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import ca.uqac.studify.data.local.StudifyDatabase
import ca.uqac.studify.data.repository.AcademicRepository
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.navigation.NavGraph
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskViewModel
import ca.uqac.studify.ui.screens.detail.DetailViewModel
import ca.uqac.studify.ui.screens.exam.AddExamViewModel
import ca.uqac.studify.ui.screens.home.HomeViewModel
import ca.uqac.studify.ui.screens.schedule.AddCourseViewModel
import ca.uqac.studify.ui.screens.schedule.ScheduleViewModel
import ca.uqac.studify.ui.theme.StudifyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Database
    private lateinit var database: StudifyDatabase

    // Repositories
    private lateinit var taskRepository: TaskRepository
    private lateinit var academicRepository: AcademicRepository

    // ViewModels
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var addEditTaskViewModel: AddEditTaskViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var addCourseViewModel: AddCourseViewModel
    private lateinit var addExamViewModel: AddExamViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Database
        database = StudifyDatabase.getDatabase(applicationContext)

        // Initialize Repositories
        taskRepository = TaskRepository(database.taskDao())
        academicRepository = AcademicRepository(
            courseDao = database.courseDao(),
            examDao = database.examDao(),
            taskDao = database.taskDao()
        )

        // Initialize ViewModels
        homeViewModel = HomeViewModel().apply {
            setRepository(taskRepository)
        }

        detailViewModel = DetailViewModel().apply {
            setRepository(taskRepository)
        }

        addEditTaskViewModel = AddEditTaskViewModel().apply {
            setRepository(taskRepository)
        }

        scheduleViewModel = ScheduleViewModel().apply {
            setRepository(academicRepository)
        }

        addCourseViewModel = AddCourseViewModel().apply {
            setRepository(academicRepository)
        }

        addExamViewModel = AddExamViewModel().apply {
            setRepository(academicRepository)
        }

        // Update tasks to next occurrence on app launch
        lifecycleScope.launch {
            taskRepository.updateTasksToNextOccurrence()
        }

        setContent {
            StudifyTheme {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    detailViewModel = detailViewModel,
                    addEditTaskViewModel = addEditTaskViewModel,
                    scheduleViewModel = scheduleViewModel,
                    addCourseViewModel = addCourseViewModel,
                    addExamViewModel = addExamViewModel
                )
            }
        }
    }
}