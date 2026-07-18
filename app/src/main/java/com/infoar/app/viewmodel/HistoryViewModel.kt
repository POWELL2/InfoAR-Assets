package com.infoar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    val allHistory: StateFlow<List<HistoryEntity>>

    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        allHistory = repository.allHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insert(history: HistoryEntity) {
        viewModelScope.launch {
            repository.insert(history)
        }
    }

    fun delete(history: HistoryEntity) {
        viewModelScope.launch {
            repository.delete(history)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
