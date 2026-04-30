package ca.uqac.studify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

    private lateinit var database: StudifyDatabase
    private lateinit var taskRepository: TaskRepository
    private lateinit var academicRepository: AcademicRepository

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var addEditTaskViewModel: AddEditTaskViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var addCourseViewModel: AddCourseViewModel
    private lateinit var addExamViewModel: AddExamViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val backgroundGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
        } else true

        if (fineLocationGranted && backgroundGranted) {
            Log.d("PERMISSION", "Toutes les permissions de localisation sont accordées")
        } else {
            Log.w("PERMISSION", "Permissions de localisation refusées")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = StudifyDatabase.getDatabase(applicationContext)
        taskRepository = TaskRepository(database.taskDao())
        academicRepository = AcademicRepository(
            courseDao = database.courseDao(),
            examDao = database.examDao(),
            taskDao = database.taskDao()
        )

        homeViewModel = HomeViewModel().apply { setRepository(taskRepository) }
        detailViewModel = DetailViewModel().apply { setRepository(taskRepository) }
        addEditTaskViewModel = AddEditTaskViewModel().apply { setRepository(taskRepository) }
        scheduleViewModel = ScheduleViewModel().apply { setRepository(academicRepository) }
        addCourseViewModel = AddCourseViewModel().apply { setRepository(academicRepository) }
        addExamViewModel = AddExamViewModel().apply { setRepository(academicRepository) }

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

        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val needToRequest = permissionsToRequest.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needToRequest) {
            locationPermissionRequest.launch(permissionsToRequest.toTypedArray())
        } else {
            Log.d("PERMISSION", "Permissions déjà accordées")
        }
    }


    fun hasFullLocationPermissions(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
        return fine && background
    }
}