package com.infoar.app.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infoar.app.data.local.db.AppDatabase
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.addedit.AddEditActivity
import com.infoar.app.ui.detail.DetailActivity
import com.infoar.app.viewmodel.CatalogViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CatalogFragment : Fragment() {

    private val viewModel: CatalogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setupFAB(view)
    }

    private fun setupRecyclerView(view: View) {
        val rvCatalog = view.findViewById<RecyclerView>(R.id.rvCatalog)
        
        val adapter = PlaceAdapter(emptyList(), { place ->
            navigateToDetail(place)
        }, { place ->
            navigateToEdit(place)
        })
        rvCatalog.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.places.collect { placesList ->
                    (rvCatalog.adapter as? PlaceAdapter)?.updateData(placesList)
                }
            }
        }
    }

    private fun setupFAB(view: View) {
        view.findViewById<FloatingActionButton>(R.id.fabAddPlace).setOnClickListener {
            startActivity(Intent(requireContext(), AddEditActivity::class.java))
        }
    }

    private fun navigateToDetail(place: PlaceEntity) {
        saveToHistory(place)
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_PLACE, place)
        }
        startActivity(intent)
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
                    category = "Desde Catálogo",
                    model3dUrl = place.model3dAsset
                )
            )
        }
    }

    private fun navigateToEdit(place: PlaceEntity) {
        val intent = Intent(requireContext(), AddEditActivity::class.java).apply {
            putExtra(AddEditActivity.EXTRA_PLACE, place)
        }
        startActivity(intent)
    }
}
