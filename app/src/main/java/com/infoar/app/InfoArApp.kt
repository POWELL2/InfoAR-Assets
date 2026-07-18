package com.infoar.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.infoar.app.data.preferences.PreferencesRepository

class InfoArApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Aplicar el tema guardado globalmente al iniciar la app
        val repository = PreferencesRepository(this)
        if (repository.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
