package com.infoar.app.ui.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.ui.addedit.AddEditActivity
import com.infoar.app.ui.detail.DetailActivity
import com.infoar.app.viewmodel.HistoryViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    // SECCIÓN 8: registerForActivityResult para recibir datos de AddEditActivity
    private val addHistoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val name = data?.getStringExtra(AddEditActivity.EXTRA_NAME) ?: ""
            val description = data?.getStringExtra(AddEditActivity.EXTRA_DESCRIPTION) ?: ""
            val category = data?.getStringExtra(AddEditActivity.EXTRA_CATEGORY) ?: "General"
            
            if (name.isNotEmpty()) {
                val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val newHistory = HistoryEntity(
                    name = name,
                    description = description,
                    date = currentDate,
                    category = category
                )
                viewModel.insert(newHistory)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        val adapter = HistoryAdapter(
            onDeleteClick = { historyItem ->
                viewModel.delete(historyItem)
            },
            onItemClick = { historyItem ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_ID, historyItem.id)
                    putExtra(DetailActivity.EXTRA_NAME, historyItem.name)
                    putExtra(DetailActivity.EXTRA_DESCRIPTION, historyItem.description)
                }
                startActivity(intent)
            }
        )
        rvHistory.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allHistory.collect { historyList ->
                    adapter.submitList(historyList)
                }
            }
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnClearHistory).setOnClickListener {
            viewModel.deleteAll()
        }

        // Lanzar AddEditActivity usando el launcher registrado
        findViewById<FloatingActionButton>(R.id.btnAddHistory).setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            addHistoryLauncher.launch(intent)
        }
    }
}
