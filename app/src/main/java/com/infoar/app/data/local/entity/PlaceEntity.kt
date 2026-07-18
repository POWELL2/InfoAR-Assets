package com.infoar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val imageUrl: String,
    val qrCode: String,
    val model3dAsset: String
) : Serializable
