package com.example.shinyhuntapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveLoggedInUserId(userId: Int) {
        prefs.edit { putInt("user_id", userId) }
    }

    fun getLoggedInUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun hasFetchedPokemon(): Boolean {
        return prefs.getBoolean("has_fetched_pokemon", false)
    }

    fun setHasFetchedPokemon(value: Boolean) {
        prefs.edit { putBoolean("has_fetched_pokemon", value) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}