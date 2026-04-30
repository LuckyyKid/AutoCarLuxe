package com.example.autodrive.presenter.contract

import com.example.autodrive.model.entity.Voiture

interface ClientContract {

    interface View {
        fun afficherVoitures(voitures: List<Voiture>)
    }

    interface Presenter {
        fun chargerDerniereRecherche(): String
        fun enregistrerDerniereRecherche(recherche: String)
        fun chargerVoitures(
            recherche: String,
            marqueFiltre: String,
            modeleFiltre: String,
            prixMinFiltre: String,
            prixMaxFiltre: String,
            anneeFiltre: String,
            seulementDisponibles: Boolean
        )
    }
}
