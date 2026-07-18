package com.infoar.app.data.repository

import com.infoar.app.data.local.dao.PlaceDao
import com.infoar.app.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

class CatalogRepository(private val placeDao: PlaceDao) {

    fun getCatalogPlaces(): Flow<List<PlaceEntity>> = placeDao.getAll()

    suspend fun insertPlace(place: PlaceEntity) = placeDao.insert(place)

    suspend fun updatePlace(place: PlaceEntity) = placeDao.update(place)

    suspend fun deletePlace(place: PlaceEntity) = placeDao.delete(place)

    suspend fun getPlaceById(id: Int) = placeDao.getById(id)

    suspend fun getPlaceByQr(qr: String) = placeDao.getByQrCode(qr)
}
