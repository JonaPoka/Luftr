package com.jonapoka.luftr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jonapoka.luftr.ui.screens.*
import com.jonapoka.luftr.viewmodel.WorkoutViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: WorkoutViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToWorkoutList = { navController.navigate(Screen.WorkoutList.route) },
                onNavigateToAIPlanner = { navController.navigate(Screen.AIPlanner.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onStartWorkout = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                }
            )
        }

        composable(Screen.WorkoutList.route) {
            WorkoutListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                }
            )
        }

        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            ActiveWorkoutScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AIPlanner.route) {
            ChatAIPlannerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onWorkoutGenerated = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                }
            )
        }
    }
}
