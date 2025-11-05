@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.jonapoka.luftr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonapoka.luftr.R
import com.jonapoka.luftr.data.entities.ExerciseSet
import com.jonapoka.luftr.data.entities.ExerciseWithSets
import com.jonapoka.luftr.ui.components.ExerciseImageCard
import com.jonapoka.luftr.ui.components.ExerciseAlternativesDialog
import com.jonapoka.luftr.ui.components.ExerciseGuidanceCard
import com.jonapoka.luftr.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    workoutId: Long,
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val exercises by viewModel.currentExercises.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exercise)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFinishDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.finish_workout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExerciseDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_exercise))
            }
        }
    ) { padding ->
        if (exercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Add your first exercise",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                
                items(exercises) { exerciseWithSets ->
                    ExerciseCard(
                        exerciseWithSets = exerciseWithSets,
                        onAddSet = { reps, weight ->
                            viewModel.addSet(exerciseWithSets.exercise.id, reps, weight)
                        },
                        onUpdateSet = { set -> viewModel.updateSet(set) },
                        onDeleteSet = { set -> viewModel.deleteSet(set) },
                        onSkipExercise = {
                            // Mark exercise as skipped by deleting it
                            viewModel.deleteExercise(exerciseWithSets.exercise.id)
                        },
                        onSwitchExercise = { newExerciseName ->
                            // Replace the exercise
                            viewModel.replaceExercise(
                                exerciseWithSets.exercise.id,
                                newExerciseName,
                                exerciseWithSets.exercise.muscleGroup
                            )
                        }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showAddExerciseDialog) {
            AddExerciseDialog(
                onDismiss = { showAddExerciseDialog = false },
                onAdd = { name, muscleGroup ->
                    viewModel.addExercise(workoutId, name, muscleGroup)
                    showAddExerciseDialog = false
                }
            )
        }

        if (showFinishDialog) {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = { Text(stringResource(R.string.finish_workout)) },
                text = { Text("Are you sure you want to finish this workout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.finishWorkout(workoutId, 60)
                            showFinishDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text(stringResource(R.string.finish_workout))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFinishDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exerciseWithSets: ExerciseWithSets,
    onAddSet: (Int, Float) -> Unit,
    onUpdateSet: (ExerciseSet) -> Unit,
    onDeleteSet: (ExerciseSet) -> Unit,
    onSkipExercise: () -> Unit = {},
    onSwitchExercise: (String) -> Unit = {}
) {
    var showAddSetDialog by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    var showAlternativesDialog by remember { mutableStateOf(false) }
    var showSkipConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseWithSets.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = exerciseWithSets.exercise.muscleGroup,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Show info icon if instructions are available
                if (!exerciseWithSets.exercise.instructions.isNullOrBlank()) {
                    IconButton(onClick = { showInstructions = !showInstructions }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Instructions",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Show exercise image/gif if available
            if (!exerciseWithSets.exercise.imageUrl.isNullOrBlank() || !exerciseWithSets.exercise.gifUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                ExerciseImageCard(
                    imageUrl = exerciseWithSets.exercise.imageUrl,
                    gifUrl = exerciseWithSets.exercise.gifUrl,
                    exerciseName = exerciseWithSets.exercise.name,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Show AI guidance when info button is clicked
            if (showInstructions) {
                Spacer(modifier = Modifier.height(12.dp))
                ExerciseGuidanceCard(
                    exerciseName = exerciseWithSets.exercise.name,
                    goal = "Build Muscle", // Default goal, could be stored with workout
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (exerciseWithSets.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.sets),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(0.5f)
                    )
                    Text(
                        text = stringResource(R.string.reps),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.weight),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                }

                exerciseWithSets.sets.forEach { set ->
                    SetRow(
                        set = set,
                        onDelete = { onDeleteSet(set) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showAddSetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_set))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Skip and Switch buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showAlternativesDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Switch")
                }
                
                OutlinedButton(
                    onClick = { showSkipConfirmDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Skip")
                }
            }
        }
    }

    if (showAddSetDialog) {
        AddSetDialog(
            onDismiss = { showAddSetDialog = false },
            onAdd = { reps, weight ->
                onAddSet(reps, weight)
                showAddSetDialog = false
            }
        )
    }
    
    if (showAlternativesDialog) {
        ExerciseAlternativesDialog(
            currentExerciseName = exerciseWithSets.exercise.name,
            muscleGroup = exerciseWithSets.exercise.muscleGroup,
            onDismiss = { showAlternativesDialog = false },
            onSelectAlternative = { newName ->
                onSwitchExercise(newName)
            }
        )
    }
    
    if (showSkipConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSkipConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            },
            title = { Text("Skip Exercise?") },
            text = {
                Text("Are you sure you want to skip ${exerciseWithSets.exercise.name}? The exercise will be removed from this workout.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSkipExercise()
                        showSkipConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkipConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SetRow(
    set: ExerciseSet,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = "${set.reps}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${set.weight} kg",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var expanded by remember { mutableStateOf(false) }
    
    val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_exercise)) },
        text = {
            Column {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMuscleGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Muscle Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        muscleGroups.forEach { muscle ->
                            DropdownMenuItem(
                                text = { Text(muscle) },
                                onClick = {
                                    selectedMuscleGroup = muscle
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (exerciseName.isNotBlank()) {
                        onAdd(exerciseName, selectedMuscleGroup)
                    }
                },
                enabled = exerciseName.isNotBlank()
            ) {
                Text(stringResource(R.string.add_exercise))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun AddSetDialog(
    onDismiss: () -> Unit,
    onAdd: (Int, Float) -> Unit
) {
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_set)) },
        text = {
            Column {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.reps)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("${stringResource(R.string.weight)} (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val repsInt = reps.toIntOrNull() ?: 0
                    val weightFloat = weight.toFloatOrNull() ?: 0f
                    if (repsInt > 0) {
                        onAdd(repsInt, weightFloat)
                    }
                }
            ) {
                Text(stringResource(R.string.add_set))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
