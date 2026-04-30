package com.example.autodrive.presenter

import com.example.autodrive.model.repository.ClientFilter
import com.example.autodrive.model.repository.VoitureRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.ClientContract

class ClientPresenter(
    private val view: ClientContract.View,
    private val voitureRepository: VoitureRepository,
    private val userSession: UserSession
) : ClientContract.Presenter {

    override fun chargerDerniereRecherche(): String {
        return userSession.getDerniereRecherche()
    }

    override fun enregistrerDerniereRecherche(recherche: String) {
        userSession.saveDerniereRecherche(recherche)
    }

    override fun chargerVoitures(
        recherche: String,
        marqueFiltre: String,
        modeleFiltre: String,
        prixMinFiltre: String,
        prixMaxFiltre: String,
        anneeFiltre: String,
        seulementDisponibles: Boolean
    ) {
        val filter = ClientFilter(
            recherche = recherche,
            marque = marqueFiltre,
            modele = modeleFiltre,
            prixMin = prixMinFiltre,
            prixMax = prixMaxFiltre,
            annee = anneeFiltre,
            seulementDisponibles = seulementDisponibles
        )
        view.afficherVoitures(voitureRepository.filtrerVoitures(filter))
    }
}
