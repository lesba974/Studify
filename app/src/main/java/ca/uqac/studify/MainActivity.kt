package ca.uqac.studify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ca.uqac.studify.data.local.StudifyDatabase
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.navigation.NavGraph
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskViewModel
import ca.uqac.studify.ui.screens.home.HomeViewModel
import ca.uqac.studify.ui.theme.StudifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Initialiser la database et le repository
        val database = StudifyDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())

        setContent {
            StudifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Créer le NavController
                    val navController = rememberNavController()

                    // Créer les ViewModels
                    val homeViewModel: HomeViewModel = viewModel()
                    val addEditViewModel: AddEditTaskViewModel = viewModel()

                    // Passer le repository aux ViewModels
                    homeViewModel.setRepository(repository)
                    addEditViewModel.setRepository(repository)

                    // Lancer la navigation
                    NavGraph(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        addEditViewModel = addEditViewModel
                    )
                }
            }
        }
    }
}