package com.example.autodrive.model.session

import android.content.Context

// AI codex
class UserSession(context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveCurrentUserId(userId: Long) {
        prefs.edit().putLong("current_user_id", userId).apply()
    }

    fun getCurrentUserId(): Long {
        return prefs.getLong("current_user_id", 1L)
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains("current_user_id")
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getDerniereRecherche(): String {
        return prefs.getString("pref_derniere_recherche", "") ?: ""
    }

    fun saveDerniereRecherche(recherche: String) {
        prefs.edit().putString("pref_derniere_recherche", recherche).apply()
    }

    fun getMarqueFavorite(): String {
        return prefs.getString("pref_marque_favorite", "") ?: ""
    }

    fun saveMarqueFavorite(marque: String) {
        prefs.edit().putString("pref_marque_favorite", marque).apply()
    }


    fun getModeAffichage(): String {
        return prefs.getString("pref_mode_affichage", "liste") ?: "liste"
    }

    fun saveModeAffichage(mode: String) {
        prefs.edit().putString("pref_mode_affichage", mode).apply()
    }
}