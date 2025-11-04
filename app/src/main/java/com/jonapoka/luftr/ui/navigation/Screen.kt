package com.jonapoka.luftr.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WorkoutList : Screen("workout_list")
    object ActiveWorkout : Screen("active_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "active_workout/$workoutId"
    }
    object AIPlanner : Screen("ai_planner")
    object History : Screen("history")
}
