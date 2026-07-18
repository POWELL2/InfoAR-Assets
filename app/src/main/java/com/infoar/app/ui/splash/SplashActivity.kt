package com.infoar.app.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.infoar.app.ui.auth.AuthActivity
import com.infoar.app.ui.home.HomeActivity
import com.utp.parcial2_proyecto.R

class SplashActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            navigateToNext()
        } else {
            Toast.makeText(this, "Se requieren permisos de Cámara y Ubicación para usar la app", Toast.LENGTH_LONG).show()
            // Continuamos de todos modos, pero las funciones estarán limitadas
            navigateToNext()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        if (allPermissionsGranted()) {
            navigateToNext()
        } else {
            requestPermissionsLauncher.launch(requiredPermissions)
        }
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToNext() {
        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            
            val intent = if (user != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
