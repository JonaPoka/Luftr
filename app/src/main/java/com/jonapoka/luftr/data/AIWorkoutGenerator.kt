package com.jonapoka.luftr.data

import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.data.entities.ExerciseSet

data class WorkoutPlan(
    val name: String,
    val exercises: List<PlannedExercise>
)

data class PlannedExercise(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restTime: Int = 60
)

object AIWorkoutGenerator {
    
    private val exerciseDatabase = mapOf(
        "Chest" to listOf(
            "Bench Press", "Incline Dumbbell Press", "Cable Flyes", 
            "Push-ups", "Dips", "Pec Deck"
        ),
        "Back" to listOf(
            "Pull-ups", "Bent Over Rows", "Lat Pulldown", 
            "Seated Cable Row", "Deadlift", "T-Bar Row"
        ),
        "Legs" to listOf(
            "Squats", "Leg Press", "Lunges", "Leg Extensions", 
            "Leg Curls", "Calf Raises", "Romanian Deadlift"
        ),
        "Shoulders" to listOf(
            "Overhead Press", "Lateral Raises", "Front Raises", 
            "Rear Delt Flyes", "Arnold Press", "Upright Row"
        ),
        "Arms" to listOf(
            "Barbell Curls", "Tricep Dips", "Hammer Curls", 
            "Skull Crushers", "Cable Curls", "Tricep Pushdowns"
        ),
        "Core" to listOf(
            "Planks", "Crunches", "Russian Twists", 
            "Leg Raises", "Cable Crunches", "Mountain Climbers"
        )
    )

    fun generateWorkout(
        goal: String,
        experienceLevel: String,
        duration: String,
        targetMuscles: List<String>
    ): WorkoutPlan {
        val exercises = mutableListOf<PlannedExercise>()
        
        // Determine number of exercises based on duration
        val exerciseCount = when (duration) {
            "30 minutes" -> 4
            "45 minutes" -> 6
            "60 minutes" -> 8
            else -> 10
        }
        
        // Determine sets and reps based on goal and experience
        val (sets, reps) = getSetRepScheme(goal, experienceLevel)
        
        // Select exercises from target muscles
        val selectedMuscles = if (targetMuscles.isEmpty()) {
            exerciseDatabase.keys.take(2).toList()
        } else {
            targetMuscles
        }
        
        val exercisesPerMuscle = exerciseCount / selectedMuscles.size.coerceAtLeast(1)
        
        selectedMuscles.forEach { muscle ->
            val muscleExercises = exerciseDatabase[muscle] ?: emptyList()
            muscleExercises.shuffled().take(exercisesPerMuscle).forEach { exerciseName ->
                exercises.add(
                    PlannedExercise(
                        name = exerciseName,
                        muscleGroup = muscle,
                        sets = sets,
                        reps = reps,
                        restTime = getRestTime(goal, experienceLevel)
                    )
                )
            }
        }
        
        // Fill remaining slots if needed
        while (exercises.size < exerciseCount) {
            val randomMuscle = selectedMuscles.random()
            val randomExercise = exerciseDatabase[randomMuscle]?.random()
            if (randomExercise != null && exercises.none { it.name == randomExercise }) {
                exercises.add(
                    PlannedExercise(
                        name = randomExercise,
                        muscleGroup = randomMuscle,
                        sets = sets,
                        reps = reps,
                        restTime = getRestTime(goal, experienceLevel)
                    )
                )
            }
        }
        
        val workoutName = generateWorkoutName(selectedMuscles, goal)
        
        return WorkoutPlan(
            name = workoutName,
            exercises = exercises.take(exerciseCount)
        )
    }
    
    private fun getSetRepScheme(goal: String, experienceLevel: String): Pair<Int, Int> {
        return when (goal) {
            "Build Muscle" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 12)
                "Intermediate" -> Pair(4, 10)
                else -> Pair(5, 8)
            }
            "Lose Weight" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 15)
                "Intermediate" -> Pair(4, 15)
                else -> Pair(4, 20)
            }
            "Get Stronger" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 8)
                "Intermediate" -> Pair(4, 6)
                else -> Pair(5, 5)
            }
            "Improve Endurance" -> when (experienceLevel) {
                "Beginner" -> Pair(3, 20)
                "Intermediate" -> Pair(4, 20)
                else -> Pair(4, 25)
            }
            else -> Pair(3, 12)
        }
    }
    
    private fun getRestTime(goal: String, experienceLevel: String): Int {
        return when (goal) {
            "Build Muscle" -> 90
            "Lose Weight" -> 45
            "Get Stronger" -> 180
            "Improve Endurance" -> 30
            else -> 60
        }
    }
    
    private fun generateWorkoutName(muscles: List<String>, goal: String): String {
        val musclesPart = when {
            muscles.size == 1 -> muscles.first()
            muscles.size == 2 -> "${muscles[0]} & ${muscles[1]}"
            else -> "Full Body"
        }
        
        val goalPart = when (goal) {
            "Build Muscle" -> "Hypertrophy"
            "Get Stronger" -> "Strength"
            "Lose Weight" -> "Fat Burn"
            "Improve Endurance" -> "Endurance"
            else -> "Workout"
        }
        
        return "$musclesPart $goalPart"
    }
}
