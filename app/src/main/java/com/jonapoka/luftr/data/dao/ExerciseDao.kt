package com.jonapoka.luftr.data.dao

import androidx.room.*
import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.data.entities.ExerciseWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY `order`")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<Exercise>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY `order`")
    fun getExercisesWithSets(workoutId: Long): Flow<List<ExerciseWithSets>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: Long)
}
