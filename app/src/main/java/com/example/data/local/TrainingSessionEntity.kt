package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val trainingType: String,
    val dateString: String,
    val timestamp: Long,
    val durationMinutes: Int,
    val heroPoints: Int,
    val rankGrade: String,
    val notes: String,
    val partnerQuote: String
)
