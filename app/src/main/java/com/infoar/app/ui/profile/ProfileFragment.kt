package com.infoar.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.infoar.app.ui.auth.AuthActivity
import com.infoar.app.viewmodel.SettingsViewModel
import com.utp.parcial2_proyecto.R
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etUserName: TextInputEditText
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var sliderArDetail: Slider
    private lateinit var rgLanguage: RadioGroup
    private lateinit var rbSpanish: RadioButton
    private lateinit var rbEnglish: RadioButton
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        loadUserDataFromFirebase()
        observeViewModel()
        setupListeners()
    }

    private fun initViews(view: View) {
        etUserName = view.findViewById(R.id.etUserName)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        sliderArDetail = view.findViewById(R.id.sliderArDetail)
        rgLanguage = view.findViewById(R.id.rgLanguage)
        rbSpanish = view.findViewById(R.id.rbSpanish)
        rbEnglish = view.findViewById(R.id.rbEnglish)
        btnSave = view.findViewById(R.id.btnSaveSettings)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun loadUserDataFromFirebase() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    etUserName.setText(name)
                    // También actualizamos el ViewModel local para que coincida
                    viewModel.saveUserName(name)
                }
            }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userName.collect { name ->
                        if (etUserName.text.toString() != name && name.isNotEmpty()) {
                            etUserName.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.isDarkMode.collect { isEnabled ->
                        if (switchDarkMode.isChecked != isEnabled) {
                            switchDarkMode.isChecked = isEnabled
                        }
                    }
                }
                launch {
                    viewModel.arDetail.collect { detail ->
                        if (sliderArDetail.value != detail) {
                            sliderArDetail.value = detail
                        }
                    }
                }
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
            val newName = etUserName.text.toString().trim()
            viewModel.saveUserName(newName)
            viewModel.saveArDetail(sliderArDetail.value)
            
            // Actualizar también en Firebase para que sea persistente
            val userId = auth.currentUser?.uid
            if (userId != null && newName.isNotEmpty()) {
                val data = mapOf("name" to newName)
                db.collection("users").document(userId).set(data, SetOptions.merge())
            }
            
            val selectedLang = if (rbEnglish.isChecked) "en" else "es"
            val currentLang = viewModel.language.value
            
            if (selectedLang != currentLang) {
                viewModel.saveLanguage(selectedLang)
                applyLanguage(selectedLang)
            } else {
                Toast.makeText(requireContext(), "Configuración guardada", Toast.LENGTH_SHORT).show()
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

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun applyLanguage(code: String) {
        val appLocales: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }
}
