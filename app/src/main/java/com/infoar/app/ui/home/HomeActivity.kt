package com.infoar.app.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.infoar.app.ui.catalog.CatalogFragment
import com.infoar.app.ui.history.HistoryFragment
import com.infoar.app.ui.map.MapFragment
import com.infoar.app.ui.profile.ProfileFragment
import com.infoar.app.ui.scan.ScanFragment
import com.utp.parcial2_proyecto.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }
        
        // Cargar fragmento inicial (Mapa)
        if (savedInstanceState == null) {
            loadFragment(MapFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_map -> MapFragment()
                R.id.nav_qr -> ScanFragment()
                R.id.nav_catalog -> CatalogFragment()
                R.id.nav_history -> HistoryFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> MapFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    override fun onBackPressed() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNavigation.selectedItemId != R.id.nav_map) {
            bottomNavigation.selectedItemId = R.id.nav_map
        } else {
            super.onBackPressed()
        }
    }
}
