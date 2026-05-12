package com.example.autodrive.presenter.contract

import com.example.autodrive.model.entity.Voiture

interface VoitureContract {

    interface View {
        fun afficherVoitures(voitures: List<Voiture>)
        fun afficherMessage(message: String)
    }

    interface Presenter {
        fun chargerVoitures()
        fun ajouterVoiture(voiture: Voiture)
        fun supprimerVoiture(id: Long)
        fun modifierVoiture(voiture: Voiture)
        fun enregistrerVoitureDepuisFormulaire(
            id: Long,
            marque: String,
            modele: String,
            annee: String,
            prixParJour: String,
            estDisponible: Boolean,
            imageUrls: String,
            description: String
        )
    }


}
