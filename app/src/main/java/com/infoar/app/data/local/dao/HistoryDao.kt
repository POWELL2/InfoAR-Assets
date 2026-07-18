package com.infoar.app.data.local.dao

import androidx.room.*
import com.infoar.app.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Delete
    suspend fun delete(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
