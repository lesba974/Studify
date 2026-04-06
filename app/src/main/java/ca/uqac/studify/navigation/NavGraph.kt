package ca.uqac.studify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.uqac.studify.ui.screens.HomeScreen
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskScreen
import ca.uqac.studify.ui.screens.addEdit.AddEditTaskViewModel
import ca.uqac.studify.ui.screens.detail.DetailScreen
import ca.uqac.studify.ui.screens.detail.DetailViewModel
import ca.uqac.studify.ui.screens.exam.AddExamScreen
import ca.uqac.studify.ui.screens.exam.AddExamViewModel
import ca.uqac.studify.ui.screens.home.HomeViewModel
import ca.uqac.studify.ui.screens.schedule.AddCourseScreen
import ca.uqac.studify.ui.screens.schedule.AddCourseViewModel
import ca.uqac.studify.ui.screens.schedule.ImportScheduleScreen
import ca.uqac.studify.ui.screens.schedule.ScheduleViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    detailViewModel: DetailViewModel,
    addEditTaskViewModel: AddEditTaskViewModel,
    scheduleViewModel: ScheduleViewModel,
    addCourseViewModel: AddCourseViewModel,
    addExamViewModel: AddExamViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onTaskClick = { taskId ->
                    navController.navigate("detail/$taskId")
                },
                onAddTaskClick = {
                    navController.navigate("addEdit/new")
                },
                onNavigateToSchedule = {
                    navController.navigate("schedule")
                },
                onNavigateToAddExam = {
                    navController.navigate("addExam/new")
                }
            )
        }

        composable(
            route = "detail/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            DetailScreen(
                viewModel = detailViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate("addEdit/$id")
                }
            )
        }

        composable(
            route = "addEdit/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            AddEditTaskScreen(
                viewModel = addEditTaskViewModel,
                taskId = if (taskId == "new") null else taskId?.toLongOrNull(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("schedule") {
            ImportScheduleScreen(
                viewModel = scheduleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddCourse = {
                    navController.navigate("addCourse/new")
                },
                onNavigateToEditCourse = { courseId ->
                    navController.navigate("addCourse/$courseId")
                }
            )
        }

        composable(
            route = "addCourse/{courseId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            AddCourseScreen(
                viewModel = addCourseViewModel,
                courseId = if (courseId == "new") null else courseId?.toLongOrNull(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "addExam/{examId}",
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId")
            AddExamScreen(
                viewModel = addExamViewModel,
                examId = if (examId == "new") null else examId?.toLongOrNull(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}