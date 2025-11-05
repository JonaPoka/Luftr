@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.jonapoka.luftr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonapoka.luftr.R
import com.jonapoka.luftr.data.AIWorkoutGenerator
import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.data.entities.ExerciseSet
import com.jonapoka.luftr.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit,
    onWorkoutGenerated: (Long) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedGoal by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf("") }
    var selectedMuscles by remember { mutableStateOf(setOf<String>()) }
    var isGenerating by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_workout_planner)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = (currentStep + 1) / 4f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (currentStep) {
                0 -> GoalSelectionStep(
                    selectedGoal = selectedGoal,
                    onGoalSelected = { selectedGoal = it }
                )
                1 -> LevelSelectionStep(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { selectedLevel = it }
                )
                2 -> DurationSelectionStep(
                    selectedDuration = selectedDuration,
                    onDurationSelected = { selectedDuration = it }
                )
                3 -> MuscleSelectionStep(
                    selectedMuscles = selectedMuscles,
                    onMusclesChanged = { selectedMuscles = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        enabled = !isGenerating
                    ) {
                        Text(stringResource(R.string.previous))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { currentStep++ },
                        enabled = when (currentStep) {
                            0 -> selectedGoal.isNotEmpty()
                            1 -> selectedLevel.isNotEmpty()
                            2 -> selectedDuration.isNotEmpty()
                            else -> true
                        }
                    ) {
                        Text(stringResource(R.string.next))
                    }
                } else {
                    Button(
                        onClick = {
                            isGenerating = true
                            scope.launch {
                                try {
                                    val workoutPlan = AIWorkoutGenerator.generateWorkout(
                                        goal = selectedGoal,
                                        experienceLevel = selectedLevel,
                                        duration = selectedDuration,
                                        targetMuscles = selectedMuscles.toList()
                                    )

                                    viewModel.createWorkout(workoutPlan.name, isAiGenerated = true) { workoutId ->
                                        // Add exercises with media
                                        val exercises = workoutPlan.exercises.map { planned ->
                                            Exercise(
                                                workoutId = workoutId,
                                                name = planned.name,
                                                muscleGroup = planned.muscleGroup,
                                                order = workoutPlan.exercises.indexOf(planned),
                                                imageUrl = planned.imageUrl,
                                                gifUrl = planned.gifUrl,
                                                instructions = planned.instructions
                                            )
                                        }
                                        viewModel.addExercises(workoutId, exercises)

                                        isGenerating = false
                                        onWorkoutGenerated(workoutId)
                                    }
                                } catch (e: Exception) {
                                    isGenerating = false
                                    // Handle error - could show a snackbar here
                                }
                            }
                        },
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(stringResource(R.string.generate_workout))
                    }
                }
            }
        }
    }
}

@Composable
fun GoalSelectionStep(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.fitness_goal),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        val goals = listOf(
            R.string.goal_muscle_gain to "Build Muscle",
            R.string.goal_lose_weight to "Lose Weight",
            R.string.goal_strength to "Get Stronger",
            R.string.goal_endurance to "Improve Endurance"
        )

        goals.forEach { (resId, value) ->
            SelectionCard(
                title = stringResource(resId),
                isSelected = selectedGoal == value,
                onClick = { onGoalSelected(value) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun LevelSelectionStep(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.experience_level),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        val levels = listOf(
            R.string.level_beginner to "Beginner",
            R.string.level_intermediate to "Intermediate",
            R.string.level_advanced to "Advanced"
        )

        levels.forEach { (resId, value) ->
            SelectionCard(
                title = stringResource(resId),
                isSelected = selectedLevel == value,
                onClick = { onLevelSelected(value) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DurationSelectionStep(
    selectedDuration: String,
    onDurationSelected: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.workout_duration),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        val durations = listOf(
            R.string.duration_30 to "30 minutes",
            R.string.duration_45 to "45 minutes",
            R.string.duration_60 to "60 minutes",
            R.string.duration_90 to "90+ minutes"
        )

        durations.forEach { (resId, value) ->
            SelectionCard(
                title = stringResource(resId),
                isSelected = selectedDuration == value,
                onClick = { onDurationSelected(value) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MuscleSelectionStep(
    selectedMuscles: Set<String>,
    onMusclesChanged: (Set<String>) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.target_muscles),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select one or more muscle groups",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        val muscles = listOf(
            R.string.muscle_chest to "Chest",
            R.string.muscle_back to "Back",
            R.string.muscle_legs to "Legs",
            R.string.muscle_shoulders to "Shoulders",
            R.string.muscle_arms to "Arms",
            R.string.muscle_core to "Core"
        )

        muscles.forEach { (resId, value) ->
            SelectionCard(
                title = stringResource(resId),
                isSelected = selectedMuscles.contains(value),
                onClick = {
                    val newSet = if (selectedMuscles.contains(value)) {
                        selectedMuscles - value
                    } else {
                        selectedMuscles + value
                    }
                    onMusclesChanged(newSet)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
