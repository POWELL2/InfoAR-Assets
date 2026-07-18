package com.infoar.app.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.ui.ar.ArViewActivity
import com.utp.parcial2_proyecto.R

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLACE = "extra_place"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val place = intent.getSerializableExtra(EXTRA_PLACE) as? PlaceEntity

        val name = place?.name ?: intent.getStringExtra(EXTRA_NAME) ?: "Sin nombre"
        val description = place?.description ?: intent.getStringExtra(EXTRA_DESCRIPTION) ?: "Sin descripción"
        val category = place?.category ?: "Sin categoría"

        findViewById<TextView>(R.id.tvDetailTitle).text = name
        findViewById<TextView>(R.id.tvDetailDescription).text = "$category\n\n$description"

        setupButtons(name, description, place)
        setupARButton(place)
    }

    private fun setupARButton(place: PlaceEntity?) {
        val btnOpenAR = findViewById<Button>(R.id.btnOpenAR)
        val modelUrl = place?.model3dAsset

        if (!modelUrl.isNullOrEmpty()) {
            btnOpenAR.isEnabled = true
            btnOpenAR.text = "Ver en Realidad Aumentada"
            btnOpenAR.setOnClickListener {
                val intent = Intent(this, ArViewActivity::class.java).apply {
                    putExtra(ArViewActivity.EXTRA_NAME, place?.name ?: "Objeto 3D")
                    putExtra(ArViewActivity.EXTRA_DESC, place?.description ?: "")
                    putExtra(ArViewActivity.EXTRA_URL, modelUrl)
                }
                startActivity(intent)
            }
        } else {
            btnOpenAR.isEnabled = false
            btnOpenAR.text = "Modelo 3D no disponible"
        }
    }

    private fun setupButtons(name: String, description: String, place: PlaceEntity?) {
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareInfo(name, description)
        }

        findViewById<Button>(R.id.btnOpenMap).setOnClickListener {
            if (place != null) {
                openInMaps(place)
            } else {
                Toast.makeText(this, "Coordenadas no disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareInfo(name: String, description: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Mira este lugar en InfoAR: $name\n$description")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun openInMaps(place: PlaceEntity) {
        val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
