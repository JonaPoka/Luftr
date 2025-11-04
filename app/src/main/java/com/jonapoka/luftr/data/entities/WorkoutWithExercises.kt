package com.jonapoka.luftr.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        entity = Exercise::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseWithSets>
)

data class ExerciseWithSets(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<ExerciseSet>
)
