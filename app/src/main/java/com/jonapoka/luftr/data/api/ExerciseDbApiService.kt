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
    suspend fun getAllExercises(
        @retrofit2.http.Header("X-RapidAPI-Key") apiKey: String,
        @retrofit2.http.Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): List<ExerciseDbExercise>
    
    @GET("exercises/name/{name}")
    suspend fun searchExerciseByName(
        @Path("name") name: String,
        @retrofit2.http.Header("X-RapidAPI-Key") apiKey: String,
        @retrofit2.http.Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): List<ExerciseDbExercise>
}
