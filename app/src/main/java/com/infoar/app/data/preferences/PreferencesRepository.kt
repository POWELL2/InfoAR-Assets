package com.infoar.app.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesRepository(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("infoar_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_NAME = "user_name"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_AR_DETAIL = "ar_detail"
        const val KEY_LANGUAGE = "language"
    }

    fun saveUserName(name: String) {
        sharedPref.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String = sharedPref.getString(KEY_USER_NAME, "") ?: ""

    fun saveDarkMode(isEnabled: Boolean) {
        sharedPref.edit().putBoolean(KEY_DARK_MODE, isEnabled).apply()
    }

    fun isDarkMode(): Boolean = sharedPref.getBoolean(KEY_DARK_MODE, false)

    fun saveArDetail(level: Float) {
        sharedPref.edit().putFloat(KEY_AR_DETAIL, level).apply()
    }

    fun getArDetail(): Float = sharedPref.getFloat(KEY_AR_DETAIL, 50f)

    fun saveLanguage(code: String) {
        sharedPref.edit().putString(KEY_LANGUAGE, code).apply()
    }

    fun getLanguage(): String = sharedPref.getString(KEY_LANGUAGE, "es") ?: "es"
}
