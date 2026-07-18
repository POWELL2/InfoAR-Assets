package com.infoar.app.data.repository

import com.infoar.app.data.local.dao.HistoryDao
import com.infoar.app.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAll()

    suspend fun insert(history: HistoryEntity) {
        historyDao.insert(history)
    }

    suspend fun delete(history: HistoryEntity) {
        historyDao.delete(history)
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }
}
