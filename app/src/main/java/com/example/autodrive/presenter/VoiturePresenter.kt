package com.example.autodrive.presenter

import com.example.autodrive.model.dao.VoitureDao
import com.example.autodrive.model.entity.Voiture
import com.example.autodrive.presenter.contract.VoitureContract

class VoiturePresenter(
    private val view: VoitureContract.View,
    private val voitureDao: VoitureDao
) : VoitureContract.Presenter {

    override fun chargerVoitures() {
        val voitures = voitureDao.getAll()
        view.afficherVoitures(voitures)
    }

    override fun ajouterVoiture(voiture: Voiture) {
        voitureDao.insert(voiture)
        chargerVoitures()
    }
}