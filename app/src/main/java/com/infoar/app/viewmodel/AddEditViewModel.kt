package com.infoar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.data.repository.CatalogRepository
import kotlinx.coroutines.launch

class AddEditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CatalogRepository

    init {
        val placeDao = AppDatabase.getDatabase(application).placeDao()
        repository = CatalogRepository(placeDao)
    }

    fun savePlace(place: PlaceEntity) {
        viewModelScope.launch {
            if (place.id == 0L) {
                repository.insertPlace(place)
            } else {
                repository.updatePlace(place)
            }
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.deletePlace(place)
        }
    }
}
