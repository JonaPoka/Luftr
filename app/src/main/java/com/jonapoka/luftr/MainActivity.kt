package com.jonapoka.luftr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jonapoka.luftr.data.LuftrDatabase
import com.jonapoka.luftr.data.repository.WorkoutRepository
import com.jonapoka.luftr.ui.navigation.NavGraph
import com.jonapoka.luftr.ui.theme.LuftrTheme
import com.jonapoka.luftr.viewmodel.WorkoutViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = LuftrDatabase.getDatabase(applicationContext)
        val repository = WorkoutRepository(
            workoutDao = database.workoutDao(),
            exerciseDao = database.exerciseDao(),
            exerciseSetDao = database.exerciseSetDao()
        )
        viewModel = WorkoutViewModel(repository)

        setContent {
            LuftrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
