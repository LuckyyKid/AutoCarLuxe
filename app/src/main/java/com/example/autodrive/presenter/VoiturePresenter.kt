package com.example.autodrive.presenter

import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.model.repository.VoitureRepository
import com.example.autodrive.presenter.contract.VoitureContract

class VoiturePresenter(
    private val view: VoitureContract.View,
    private val voitureRepository: VoitureRepository
) : VoitureContract.Presenter {

    override fun chargerVoitures() {
        val voitures = voitureRepository.getAll()
        view.afficherVoitures(voitures)
    }

    override fun ajouterVoiture(voiture: Voiture) {
        voitureRepository.insert(voiture)
        chargerVoitures()
    }

    override fun supprimerVoiture(id: Long) {
        voitureRepository.deleteById(id)
        chargerVoitures()
    }

    override fun modifierVoiture(voiture: Voiture) {
        voitureRepository.update(voiture)
        chargerVoitures()
    }

    override fun enregistrerVoitureDepuisFormulaire(
        id: Long,
        marque: String,
        modele: String,
        annee: String,
        prixParJour: String,
        estDisponible: Boolean,
        imageUrls: String,
        description: String
    ) {
        val anneeNumerique = annee.toIntOrNull()
        val prixNumerique = prixParJour.toDoubleOrNull()

        when {
            marque.isBlank() -> view.afficherMessage("Veuillez entrer une marque.")
            modele.isBlank() -> view.afficherMessage("Veuillez entrer un modele.")
            anneeNumerique == null -> view.afficherMessage("Veuillez entrer une annee valide.")
            prixNumerique == null -> view.afficherMessage("Veuillez entrer un prix valide.")
            prixNumerique <= 0.0 -> view.afficherMessage("Le prix doit etre superieur a 0.")
            else -> {
                val voiture = Voiture(
                    id = id,
                    marque = marque.trim(),
                    modele = modele.trim(),
                    annee = anneeNumerique,
                    prixParJour = prixNumerique,
                    estDisponible = estDisponible,
                    imageUrls = imageUrls.trim().ifBlank { null },
                    description = description.trim().ifBlank { null },
                    ageMinimum = 18
                )

                if (id == 0L) {
                    ajouterVoiture(voiture)
                } else {
                    modifierVoiture(voiture)
                }
            }
        }
    }
}
