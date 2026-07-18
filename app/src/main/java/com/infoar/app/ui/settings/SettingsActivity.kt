package com.infoar.app.ui.settings

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.infoar.app.viewmodel.SettingsViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var etUserName: TextInputEditText
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var sliderArDetail: Slider
    private lateinit var rgLanguage: RadioGroup
    private lateinit var rbSpanish: RadioButton
    private lateinit var rbEnglish: RadioButton
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        observeViewModel()
        setupListeners()
    }

    private fun initViews() {
        etUserName = findViewById(R.id.etUserName)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        sliderArDetail = findViewById(R.id.sliderArDetail)
        rgLanguage = findViewById(R.id.rgLanguage)
        rbSpanish = findViewById(R.id.rbSpanish)
        rbEnglish = findViewById(R.id.rbEnglish)
        btnSave = findViewById(R.id.btnSaveSettings)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar nombre de usuario
                launch {
                    viewModel.userName.collect { name ->
                        if (etUserName.text.toString() != name) {
                            etUserName.setText(name)
                        }
                    }
                }
                // Observar modo oscuro
                launch {
                    viewModel.isDarkMode.collect { isEnabled ->
                        if (switchDarkMode.isChecked != isEnabled) {
                            switchDarkMode.isChecked = isEnabled
                        }
                    }
                }
                // Observar detalle AR
                launch {
                    viewModel.arDetail.collect { detail ->
                        if (sliderArDetail.value != detail) {
                            sliderArDetail.value = detail
                        }
                    }
                }
                // Observar idioma
                launch {
                    viewModel.language.collect { code ->
                        if (code == "en") {
                            rbEnglish.isChecked = true
                        } else {
                            rbSpanish.isChecked = true
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            viewModel.saveUserName(etUserName.text.toString())
            viewModel.saveArDetail(sliderArDetail.value)
            
            val selectedLang = if (rbEnglish.isChecked) "en" else "es"
            val currentLang = viewModel.language.value
            
            if (selectedLang != currentLang) {
                viewModel.saveLanguage(selectedLang)
                applyLanguage(selectedLang)
            } else {
                Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        switchDarkMode.setOnClickListener {
            val isChecked = switchDarkMode.isChecked
            viewModel.saveDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun applyLanguage(code: String) {
        val appLocales: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }
}
