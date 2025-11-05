package com.jonapoka.luftr.data

import com.jonapoka.luftr.data.api.ApiClient
import com.jonapoka.luftr.data.api.ExerciseDbExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExerciseMediaFetcher {
    
    // Cache for exercise data
    private val exerciseCache = mutableMapOf<String, ExerciseDbExercise>()
    
    /**
     * Fetches exercise media (images, gifs, instructions) from ExerciseDB API
     * Falls back to placeholder if API fails
     */
    suspend fun fetchExerciseMedia(exerciseName: String): ExerciseMedia = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            if (exerciseCache.containsKey(exerciseName.lowercase())) {
                return@withContext mapToExerciseMedia(exerciseCache[exerciseName.lowercase()]!!)
            }
            
            // Try to fetch from API
            val searchName = exerciseName.lowercase().replace(" ", "%20")
            val results = ApiClient.exerciseDbApi.searchExerciseByName(
                name = searchName
            )
            
            if (results.isNotEmpty()) {
                val exercise = results.first()
                exerciseCache[exerciseName.lowercase()] = exercise
                return@withContext mapToExerciseMedia(exercise)
            }
            
            // Return placeholder if not found
            getPlaceholderMedia(exerciseName)
        } catch (e: Exception) {
            // Return placeholder if API fails
            getPlaceholderMedia(exerciseName)
        }
    }
    
    private fun mapToExerciseMedia(exercise: ExerciseDbExercise): ExerciseMedia {
        return ExerciseMedia(
            imageUrl = exercise.gifUrl,
            gifUrl = exercise.gifUrl,
            instructions = exercise.instructions.joinToString("\n")
        )
    }
    
    private fun getPlaceholderMedia(exerciseName: String): ExerciseMedia {
        // Generate placeholder images based on exercise name
        val muscleGroup = detectMuscleGroup(exerciseName)
        return ExerciseMedia(
            imageUrl = "https://via.placeholder.com/400x300/6200EE/FFFFFF?text=${exerciseName.replace(" ", "+")}",
            gifUrl = null,
            instructions = "1. Position yourself correctly\n2. Perform the movement with control\n3. Focus on proper form\n4. Breathe naturally throughout"
        )
    }
    
    private fun detectMuscleGroup(exerciseName: String): String {
        val nameLower = exerciseName.lowercase()
        return when {
            nameLower.contains("bench") || nameLower.contains("chest") || nameLower.contains("pec") -> "Chest"
            nameLower.contains("pull") || nameLower.contains("row") || nameLower.contains("lat") -> "Back"
            nameLower.contains("squat") || nameLower.contains("leg") || nameLower.contains("lunge") -> "Legs"
            nameLower.contains("shoulder") || nameLower.contains("press") || nameLower.contains("raise") -> "Shoulders"
            nameLower.contains("curl") || nameLower.contains("tricep") || nameLower.contains("arm") -> "Arms"
            nameLower.contains("core") || nameLower.contains("crunch") || nameLower.contains("plank") -> "Core"
            else -> "General"
        }
    }
}

data class ExerciseMedia(
    val imageUrl: String?,
    val gifUrl: String?,
    val instructions: String?
)
