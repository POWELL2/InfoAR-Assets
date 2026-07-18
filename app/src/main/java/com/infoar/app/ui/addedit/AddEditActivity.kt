package com.infoar.app.ui.addedit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.infoar.app.data.local.entity.PlaceEntity
import com.infoar.app.viewmodel.AddEditViewModel
import com.utp.parcial2_proyecto.R

class AddEditActivity : AppCompatActivity() {

    private val viewModel: AddEditViewModel by viewModels()
    private var existingPlace: PlaceEntity? = null

    private lateinit var etName: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etQrCode: TextInputEditText
    private lateinit var etLatitude: TextInputEditText
    private lateinit var etLongitude: TextInputEditText
    private lateinit var etModelAsset: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        existingPlace = intent.getSerializableExtra(EXTRA_PLACE) as? PlaceEntity
        
        initViews()
        setupUI()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etDescription = findViewById(R.id.etDescription)
        etCategory = findViewById(R.id.etCategory)
        etQrCode = findViewById(R.id.etQrCode)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        etModelAsset = findViewById(R.id.etModelAsset)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setupUI() {
        existingPlace?.let {
            etName.setText(it.name)
            etDescription.setText(it.description)
            etCategory.setText(it.category)
            etQrCode.setText(it.qrCode)
            etLatitude.setText(it.latitude.toString())
            etLongitude.setText(it.longitude.toString())
            etModelAsset.setText(it.model3dAsset)
            
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                viewModel.deletePlace(existingPlace!!)
                Toast.makeText(this, "Lugar eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnSave.setOnClickListener {
            if (validateFields()) {
                val name = etName.text.toString()
                val description = etDescription.text.toString()
                val category = etCategory.text.toString()
                val qrCode = etQrCode.text.toString()
                val lat = etLatitude.text.toString().toDoubleOrNull() ?: 0.0
                val lon = etLongitude.text.toString().toDoubleOrNull() ?: 0.0
                val model = etModelAsset.text.toString().takeIf { it.isNotBlank() } ?: ""

                val placeToSave = existingPlace?.copy(
                    name = name,
                    description = description,
                    category = category,
                    qrCode = qrCode,
                    latitude = lat,
                    longitude = lon,
                    model3dAsset = model
                ) ?: PlaceEntity(
                    name = name,
                    description = description,
                    category = category,
                    qrCode = qrCode,
                    latitude = lat,
                    longitude = lon,
                    imageUrl = "",
                    model3dAsset = model
                )

                viewModel.savePlace(placeToSave)
                Toast.makeText(this, "Lugar guardado con éxito", Toast.LENGTH_SHORT).show()

                val resultIntent = Intent().apply {
                    putExtra(EXTRA_NAME, name)
                    putExtra(EXTRA_DESCRIPTION, description)
                    putExtra(EXTRA_CATEGORY, category)
                }
                setResult(RESULT_OK, resultIntent)

                finish()
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (etName.text.isNullOrBlank()) {
            etName.error = "Campo obligatorio"
            isValid = false
        }
        if (etQrCode.text.isNullOrBlank()) {
            etQrCode.error = "Campo obligatorio"
            isValid = false
        }
        if (etLatitude.text.isNullOrBlank()) {
            etLatitude.error = "Campo obligatorio"
            isValid = false
        }
        if (etLongitude.text.isNullOrBlank()) {
            etLongitude.error = "Campo obligatorio"
            isValid = false
        }
        return isValid
    }

    companion object {
        const val EXTRA_PLACE = "extra_place"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_CATEGORY = "extra_category"
    }
}
