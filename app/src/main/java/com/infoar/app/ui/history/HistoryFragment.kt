package com.infoar.app.ui.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.addedit.AddEditActivity
import com.infoar.app.ui.detail.DetailActivity
import com.infoar.app.viewmodel.HistoryViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setupButtons(view)
    }

    private fun setupRecyclerView(view: View) {
        val rvHistory = view.findViewById<RecyclerView>(R.id.rvHistory)
        val adapter = HistoryAdapter(
            onDeleteClick = { historyItem ->
                viewModel.delete(historyItem)
            },
            onItemClick = { historyItem ->
                // Reconstruir un PlaceEntity para que DetailActivity tenga todo lo necesario
                val place = PlaceEntity(
                    id = historyItem.id,
                    name = historyItem.name,
                    description = historyItem.description,
                    latitude = 0.0,
                    longitude = 0.0,
                    category = historyItem.category,
                    imageUrl = historyItem.imageUrl,
                    qrCode = "",
                    model3dAsset = historyItem.model3dUrl // ¡Aquí estaba el fallo!
                )
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_PLACE, place)
                }
                startActivity(intent)
            }
        )
        rvHistory.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allHistory.collect { historyList ->
                    adapter.submitList(historyList)
                }
            }
        }
    }

    private fun setupButtons(view: View) {
        view.findViewById<Button>(R.id.btnClearHistory).setOnClickListener {
            viewModel.deleteAll()
        }

        view.findViewById<FloatingActionButton>(R.id.btnAddHistory).setOnClickListener {
            val intent = Intent(requireContext(), AddEditActivity::class.java)
            startActivity(intent)
        }
    }
}
