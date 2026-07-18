package com.infoar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.infoar.app.data.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    private val _userName = MutableStateFlow(repository.getUserName())
    private val _isDarkMode = MutableStateFlow(repository.isDarkMode())
    private val _arDetail = MutableStateFlow(repository.getArDetail())
    private val _language = MutableStateFlow(repository.getLanguage())

    val userName: StateFlow<String> = _userName.asStateFlow()
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    val arDetail: StateFlow<Float> = _arDetail.asStateFlow()
    val language: StateFlow<String> = _language.asStateFlow()

    // Nueva función para actualización inmediata (feedback visual)
    fun setDarkMode(isEnabled: Boolean) {
        _isDarkMode.value = isEnabled
    }

    fun saveUserName(name: String) {
        repository.saveUserName(name)
        _userName.value = name
    }

    fun saveDarkMode(isEnabled: Boolean) {
        repository.saveDarkMode(isEnabled)
        _isDarkMode.value = isEnabled
    }

    fun saveArDetail(level: Float) {
        repository.saveArDetail(level)
        _arDetail.value = level
    }

    fun saveLanguage(code: String) {
        repository.saveLanguage(code)
        _language.value = code
    }
}
