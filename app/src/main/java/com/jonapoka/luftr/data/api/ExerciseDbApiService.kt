package com.jonapoka.luftr.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class ExerciseDbExercise(
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val target: String,
    val instructions: List<String>
)

interface ExerciseDbApiService {
    @GET("exercises")
    suspend fun getAllExercises(): List<ExerciseDbExercise>
    
    @GET("exercises/name/{name}")
    suspend fun searchExerciseByName(
        @Path("name") name: String
    ): List<ExerciseDbExercise>
}
