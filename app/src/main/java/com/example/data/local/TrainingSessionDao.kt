package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {
    @Query("SELECT * FROM training_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<TrainingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity): Long

    @Delete
    suspend fun deleteSession(session: TrainingSessionEntity)

    @Query("SELECT COUNT(*) FROM training_sessions")
    fun getSessionCount(): Flow<Int>

    @Query("SELECT SUM(heroPoints) FROM training_sessions")
    fun getTotalHeroPoints(): Flow<Int?>
}
