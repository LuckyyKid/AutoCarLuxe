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
}
