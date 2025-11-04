package com.jonapoka.luftr.data.repository

import com.jonapoka.luftr.data.dao.ExerciseDao
import com.jonapoka.luftr.data.dao.ExerciseSetDao
import com.jonapoka.luftr.data.dao.WorkoutDao
import com.jonapoka.luftr.data.entities.*
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseSetDao: ExerciseSetDao
) {
    // Workout operations
    fun getAllWorkouts(): Flow<List<Workout>> = workoutDao.getAllWorkouts()

    fun getRecentWorkouts(limit: Int = 5): Flow<List<WorkoutWithExercises>> =
        workoutDao.getRecentWorkouts(limit)

    suspend fun getWorkoutWithExercises(workoutId: Long): WorkoutWithExercises? =
        workoutDao.getWorkoutWithExercises(workoutId)

    suspend fun insertWorkout(workout: Workout): Long =
        workoutDao.insertWorkout(workout)

    suspend fun updateWorkout(workout: Workout) =
        workoutDao.updateWorkout(workout)

    suspend fun deleteWorkout(workout: Workout) =
        workoutDao.deleteWorkout(workout)

    // Exercise operations
    fun getExercisesWithSets(workoutId: Long): Flow<List<ExerciseWithSets>> =
        exerciseDao.getExercisesWithSets(workoutId)

    suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insertExercise(exercise)

    suspend fun insertExercises(exercises: List<Exercise>) =
        exerciseDao.insertExercises(exercises)

    suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.deleteExercise(exercise)

    // Set operations
    fun getSetsForExercise(exerciseId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsForExercise(exerciseId)

    suspend fun insertSet(set: ExerciseSet): Long =
        exerciseSetDao.insertSet(set)

    suspend fun insertSets(sets: List<ExerciseSet>) =
        exerciseSetDao.insertSets(sets)

    suspend fun updateSet(set: ExerciseSet) =
        exerciseSetDao.updateSet(set)

    suspend fun deleteSet(set: ExerciseSet) =
        exerciseSetDao.deleteSet(set)
}
