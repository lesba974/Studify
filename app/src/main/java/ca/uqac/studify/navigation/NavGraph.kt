package ca.uqac.studify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskScreen
import ca.uqac.studify.ui.screens.home.StudifyScreen
import ca.uqac.studify.ui.screens.home.HomeViewModel
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEditTask : Screen("add_edit_task/{taskId}") {
        fun createRoute(taskId: Long? = null): String {
            return if (taskId == null) "add_edit_task/new"
            else "add_edit_task/$taskId"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    addEditViewModel: AddEditTaskViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            StudifyScreen(
                viewModel = homeViewModel,
                onAddTaskClick = {
                    addEditViewModel.resetForm()
                    navController.navigate(Screen.AddEditTask.createRoute())
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.AddEditTask.createRoute(taskId))
                }
            )
        }

        composable(
            route = "add_edit_task/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val taskIdString = backStackEntry.arguments?.getString("taskId")
            val taskId = if (taskIdString == "new") null else taskIdString?.toLongOrNull()

            AddEditTaskScreen(
                viewModel = addEditViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}