package com.example.autodrive.presenter.contract

import com.example.autodrive.model.entity.Voiture

interface VoitureContract {

    interface View {
        fun afficherVoitures(voitures: List<Voiture>)
    }

    interface Presenter {
        fun chargerVoitures()
        fun ajouterVoiture(voiture: Voiture)
        fun supprimerVoiture(id: Long)
    }


}