package com.infoar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val date: String,
    val category: String,
    val imageUrl: String = "",
    val model3dUrl: String = "" // Nuevo campo para que AR funcione desde el historial
)
