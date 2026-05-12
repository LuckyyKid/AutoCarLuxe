package com.example.autodrive.presenter.contract

data class ProfilUiState(
    val nomComplet: String,
    val email: String,
    val nombreReservations: Int,
    val totalDepense: Double,
    val marqueFavorite: String,
    val marquesDisponibles: List<String>
)

interface ProfilContract {

    interface View {
        fun afficherProfil(profil: ProfilUiState)
        fun ouvrirConnexion()
    }

    interface Presenter {
        fun chargerProfil()
        fun changerMarqueFavorite(marque: String)
        fun deconnecter()
    }
}
