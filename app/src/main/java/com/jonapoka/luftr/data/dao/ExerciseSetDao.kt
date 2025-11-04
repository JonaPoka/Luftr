package com.jonapoka.luftr.data.dao

import androidx.room.*
import com.jonapoka.luftr.data.entities.ExerciseSet
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {
    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber")
    fun getSetsForExercise(exerciseId: Long): Flow<List<ExerciseSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSet>)

    @Update
    suspend fun updateSet(set: ExerciseSet)

    @Delete
    suspend fun deleteSet(set: ExerciseSet)

    @Query("DELETE FROM exercise_sets WHERE id = :setId")
    suspend fun deleteSetById(setId: Long)
}
