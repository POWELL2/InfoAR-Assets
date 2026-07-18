package com.infoar.app.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.detail.DetailActivity
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        
        enableMyLocationIfGranted()
        loadPlacesFromFirestore()

        mMap.setOnInfoWindowClickListener { marker ->
            val place = marker.tag as? PlaceEntity
            if (place != null) {
                saveToHistory(place)
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_PLACE, place)
                }
                startActivity(intent)
            }
        }
    }

    private fun saveToHistory(place: PlaceEntity) {
        val context = context ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context.applicationContext)
            database.historyDao().insert(
                HistoryEntity(
                    name = place.name,
                    description = place.description,
                    date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                    category = "Desde Mapa",
                    model3dUrl = place.model3dAsset
                )
            )
        }
    }

    private fun enableMyLocationIfGranted() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                // Ignore
            }
        }
    }

    private fun loadPlacesFromFirestore() {
        db.collection("places").addSnapshotListener { value, error ->
            if (error != null || value == null) return@addSnapshotListener
            
            mMap.clear()
            val boundsBuilder = LatLngBounds.Builder()
            var hasPlaces = false

            for (doc in value.documents) {
                val name = doc.getString("name") ?: ""
                val lat = doc.getDouble("latitude") ?: 0.0
                val lng = doc.getDouble("longitude") ?: 0.0
                
                if (lat != 0.0 && lng != 0.0) {
                    val pos = LatLng(lat, lng)
                    val place = PlaceEntity(
                        id = 0,
                        name = name,
                        description = doc.getString("description") ?: "",
                        latitude = lat,
                        longitude = lng,
                        category = doc.getString("category") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        qrCode = doc.getString("qrCode") ?: "",
                        model3dAsset = doc.getString("model3dUrl") ?: ""
                    )
                    
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(name)
                            .snippet(place.category)
                    )
                    marker?.tag = place
                    boundsBuilder.include(pos)
                    hasPlaces = true
                }
            }

            if (hasPlaces) {
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
                } catch (e: IllegalStateException) {
                    // Builder empty or map not ready
                }
            }
        }
    }
}
