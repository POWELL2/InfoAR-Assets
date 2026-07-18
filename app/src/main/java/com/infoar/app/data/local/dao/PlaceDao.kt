package com.infoar.app.data.local.dao

import androidx.room.*
import com.infoar.app.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Update
    suspend fun update(place: PlaceEntity)

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAll(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE qrCode = :qrCode LIMIT 1")
    suspend fun getByQrCode(qrCode: String): PlaceEntity?

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)
}
