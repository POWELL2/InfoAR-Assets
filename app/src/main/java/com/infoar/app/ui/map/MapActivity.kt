package com.infoar.app.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.detail.DetailActivity
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val placesList = mutableListOf<PlaceEntity>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<android.view.View>(R.id.fabBack).setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        
        checkLocationPermission()
        loadPlaces()

        mMap.setOnInfoWindowClickListener { marker ->
            val place = marker.tag as? PlaceEntity
            if (place != null) {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_PLACE, place)
                }
                startActivity(intent)
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun loadPlaces() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val places = db.placeDao().getAll().first()
            
            if (places.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                
                places.forEach { place ->
                    val pos = LatLng(place.latitude, place.longitude)
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(place.name)
                            .snippet(place.category)
                    )
                    marker?.tag = place
                    boundsBuilder.include(pos)
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
            }
        }
    }
}
