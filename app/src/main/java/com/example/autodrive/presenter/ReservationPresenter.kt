package com.example.autodrive.presenter

import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.ReservationContract

class ReservationPresenter(
    private val view: ReservationContract.View,
    private val reservationRepository: ReservationRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val userSession: UserSession
) : ReservationContract.Presenter {

    override fun initialiserUtilisateur() {
        val utilisateur = utilisateurRepository.getOrCreateUtilisateurTest()
        userSession.saveCurrentUserId(utilisateur.id)
    }

    override fun calculerCout(dateDebut: String?, dateFin: String?, prixParJour: Double) {
        view.afficherCout(
            reservationRepository.calculerCout(dateDebut, dateFin, prixParJour)
        )
    }

    override fun confirmerReservation(
        voitureId: Long,
        disponible: Boolean,
        dateDebut: String?,
        dateFin: String?,
        prixParJour: Double
    ) {
        val resultat = reservationRepository.creerReservation(
            userId = userSession.getCurrentUserId(),
            voitureId,
            disponible = disponible,
            dateDebut = dateDebut,
            dateFin = dateFin,
            prixParJour = prixParJour
        )

        if (resultat.succes) {
            view.reservationConfirmee()
        } else {
            view.afficherMessage(resultat.message)
        }
    }
}
