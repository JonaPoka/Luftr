package com.jonapoka.luftr.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val duration: Int = 0, // in minutes
    val notes: String = "",
    val isAiGenerated: Boolean = false
)
