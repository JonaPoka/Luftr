package com.jonapoka.luftr.data.dao

import androidx.room.*
import com.jonapoka.luftr.data.entities.Workout
import com.jonapoka.luftr.data.entities.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): Workout?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutWithExercises(workoutId: Long): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC LIMIT :limit")
    fun getRecentWorkouts(limit: Int = 5): Flow<List<WorkoutWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkoutById(workoutId: Long)
}
