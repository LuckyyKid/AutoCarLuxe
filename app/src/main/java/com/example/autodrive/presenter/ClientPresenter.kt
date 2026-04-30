package com.example.autodrive.presenter

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
        var resultat = if (recherche.isNotBlank()) {
            voitureRepository.rechercherVoitures(recherche)
        } else {
            voitureRepository.getAll()
        }

        if (marqueFiltre.isNotBlank()) {
            resultat = resultat.filter {
                it.marque.contains(marqueFiltre, ignoreCase = true)
            }
        }

        if (modeleFiltre.isNotBlank()) {
            resultat = resultat.filter {
                it.modele.contains(modeleFiltre, ignoreCase = true)
            }
        }

        val prixMin = prixMinFiltre.toDoubleOrNull()
        val prixMax = prixMaxFiltre.toDoubleOrNull()

        if (prixMin != null) {
            resultat = resultat.filter { it.prixParJour >= prixMin }
        }

        if (prixMax != null) {
            resultat = resultat.filter { it.prixParJour <= prixMax }
        }

        if (anneeFiltre.isNotBlank()) {
            resultat = resultat.filter { it.annee.toString() == anneeFiltre }
        }

        if (seulementDisponibles) {
            resultat = resultat.filter { it.estDisponible }
        }

        view.afficherVoitures(resultat)
    }
}
