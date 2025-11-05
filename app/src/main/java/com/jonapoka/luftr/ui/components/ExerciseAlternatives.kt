package com.jonapoka.luftr.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ExerciseAlternative(
    val name: String,
    val muscleGroup: String,
    val reason: String,
    val difficulty: String = "Similar"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseAlternativesDialog(
    currentExerciseName: String,
    muscleGroup: String,
    onDismiss: () -> Unit,
    onSelectAlternative: (String) -> Unit
) {
    val alternatives = remember(muscleGroup) {
        getExerciseAlternatives(muscleGroup)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    "Switch Exercise",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Machine taken? Try one of these alternatives:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alternatives) { alternative ->
                    AlternativeExerciseCard(
                        alternative = alternative,
                        onClick = {
                            onSelectAlternative(alternative.name)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativeExerciseCard(
    alternative: ExerciseAlternative,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
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
                Text(
                    text = alternative.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Select",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = alternative.muscleGroup,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = alternative.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "💡 ${alternative.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getExerciseAlternatives(muscleGroup: String): List<ExerciseAlternative> {
    return when (muscleGroup.lowercase()) {
        "chest" -> listOf(
            ExerciseAlternative(
                "Dumbbell Bench Press",
                "Chest",
                "Great alternative that allows independent arm movement and better range of motion"
            ),
            ExerciseAlternative(
                "Push-ups",
                "Chest",
                "No equipment needed! Bodyweight exercise that's effective anywhere",
                "Easier"
            ),
            ExerciseAlternative(
                "Cable Flyes",
                "Chest",
                "Constant tension throughout the movement with adjustable angles"
            ),
            ExerciseAlternative(
                "Incline Dumbbell Press",
                "Chest",
                "Targets upper chest, good variation if flat bench is taken"
            )
        )
        "back" -> listOf(
            ExerciseAlternative(
                "Dumbbell Rows",
                "Back",
                "Single-arm option allows you to focus on each side independently"
            ),
            ExerciseAlternative(
                "Lat Pulldown",
                "Back",
                "Similar movement pattern to pull-ups with adjustable weight"
            ),
            ExerciseAlternative(
                "Cable Rows",
                "Back",
                "Constant tension with various grip options available"
            ),
            ExerciseAlternative(
                "Inverted Rows",
                "Back",
                "Bodyweight alternative using just a bar or TRX",
                "Easier"
            )
        )
        "legs" -> listOf(
            ExerciseAlternative(
                "Goblet Squats",
                "Legs",
                "Easier to maintain form, great for quad development"
            ),
            ExerciseAlternative(
                "Bulgarian Split Squats",
                "Legs",
                "Unilateral movement that improves balance and targets each leg"
            ),
            ExerciseAlternative(
                "Leg Press",
                "Legs",
                "Machine-based alternative with back support and heavy loading option"
            ),
            ExerciseAlternative(
                "Lunges",
                "Legs",
                "Functional movement that works each leg separately"
            )
        )
        "shoulders" -> listOf(
            ExerciseAlternative(
                "Dumbbell Shoulder Press",
                "Shoulders",
                "Allows natural movement path and independent arm work"
            ),
            ExerciseAlternative(
                "Arnold Press",
                "Shoulders",
                "Adds rotation for complete shoulder activation"
            ),
            ExerciseAlternative(
                "Cable Lateral Raises",
                "Shoulders",
                "Constant tension throughout the movement"
            ),
            ExerciseAlternative(
                "Pike Push-ups",
                "Shoulders",
                "Bodyweight alternative that targets shoulders effectively",
                "Easier"
            )
        )
        "arms" -> listOf(
            ExerciseAlternative(
                "Dumbbell Curls",
                "Arms",
                "Classic bicep builder with multiple grip variations"
            ),
            ExerciseAlternative(
                "Cable Curls",
                "Arms",
                "Constant tension keeps muscle engaged throughout"
            ),
            ExerciseAlternative(
                "Overhead Tricep Extension",
                "Arms",
                "Great tricep isolation with stretch at the bottom"
            ),
            ExerciseAlternative(
                "Close-Grip Push-ups",
                "Arms",
                "Bodyweight option that effectively targets triceps",
                "Easier"
            )
        )
        "core" -> listOf(
            ExerciseAlternative(
                "Plank Variations",
                "Core",
                "Isometric hold that builds core stability"
            ),
            ExerciseAlternative(
                "Cable Crunches",
                "Core",
                "Allows progressive overload with adjustable weight"
            ),
            ExerciseAlternative(
                "Hanging Leg Raises",
                "Core",
                "Advanced movement targeting lower abs",
                "Harder"
            ),
            ExerciseAlternative(
                "Mountain Climbers",
                "Core",
                "Dynamic movement that also raises heart rate"
            )
        )
        else -> listOf(
            ExerciseAlternative(
                "Bodyweight Alternative",
                muscleGroup,
                "Ask a trainer for a suitable bodyweight exercise"
            )
        )
    }
}
