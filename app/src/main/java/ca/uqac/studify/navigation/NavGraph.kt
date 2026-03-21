package ca.uqac.studify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskScreen
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskViewModel
import ca.uqac.studify.ui.screens.detail.DetailScreen
import ca.uqac.studify.ui.screens.detail.DetailViewModel
import ca.uqac.studify.ui.screens.home.HomeScreen
import ca.uqac.studify.ui.screens.home.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Detail : Screen("detail/{taskId}") {
        fun createRoute(taskId: Long): String = "detail/$taskId"
    }

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
    detailViewModel: DetailViewModel,
    addEditViewModel: AddEditTaskViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onAddTaskClick = {
                    addEditViewModel.resetForm()
                    navController.navigate(Screen.AddEditTask.createRoute())
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.Detail.createRoute(taskId))
                }
            )
        }

        composable(
            route = "detail/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L

            DetailScreen(
                viewModel = detailViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AddEditTask.createRoute(id))
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

            if (taskId != null) {
                addEditViewModel.loadTask(taskId)
            } else {
                addEditViewModel.resetForm()
            }

            AddEditTaskScreen(
                viewModel = addEditViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}