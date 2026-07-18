package com.infoar.app.ui.catalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.addedit.AddEditActivity
import com.infoar.app.ui.detail.DetailActivity
import com.infoar.app.viewmodel.CatalogViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.launch

class CatalogActivity : AppCompatActivity() {

    private val viewModel: CatalogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        setupRecyclerView()
        setupFAB()
    }

    private fun setupRecyclerView() {
        val rvCatalog = findViewById<RecyclerView>(R.id.rvCatalog)
        
        // El adaptador se inicializa vacío
        val adapter = PlaceAdapter(emptyList(), { place ->
            navigateToDetail(place)
        }, { place ->
            navigateToEdit(place)
        })
        rvCatalog.adapter = adapter

        // Observar el StateFlow del ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.places.collect { placesList ->
                    // Actualizar el adaptador
                    (rvCatalog.adapter as? PlaceAdapter)?.updateData(placesList)
                }
            }
        }
    }

    private fun setupFAB() {
        findViewById<FloatingActionButton>(R.id.fabAddPlace).setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    private fun navigateToDetail(place: PlaceEntity) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_PLACE, place)
        }
        startActivity(intent)
    }

    private fun navigateToEdit(place: PlaceEntity) {
        val intent = Intent(this, AddEditActivity::class.java).apply {
            putExtra(AddEditActivity.EXTRA_PLACE, place)
        }
        startActivity(intent)
    }
}
