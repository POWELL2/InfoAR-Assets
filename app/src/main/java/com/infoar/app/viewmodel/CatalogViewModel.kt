package com.infoar.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.infoar.app.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CatalogViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _places = MutableStateFlow<List<PlaceEntity>>(emptyList())
    val places: StateFlow<List<PlaceEntity>> = _places

    init {
        fetchPlaces()
    }

    private fun fetchPlaces() {
        db.collection("places")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                val placesList = value?.documents?.mapNotNull { doc ->
                    PlaceEntity(
                        id = 0, // El ID se ignora para Firestore, usamos el hash solo si es necesario en Room
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        category = doc.getString("category") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        qrCode = doc.getString("qrCode") ?: "",
                        model3dAsset = doc.getString("model3dUrl") ?: ""
                    )
                } ?: emptyList()
                
                _places.value = placesList
            }
    }
}
