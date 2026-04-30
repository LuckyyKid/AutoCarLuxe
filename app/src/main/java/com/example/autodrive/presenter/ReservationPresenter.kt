package com.example.autodrive.presenter

import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.ReservationContract
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
        if (dateDebut == null || dateFin == null) {
            view.afficherCout(0.0)
            return
        }

        val debut = LocalDate.parse(dateDebut)
        val fin = LocalDate.parse(dateFin)
        val jours = ChronoUnit.DAYS.between(debut, fin)

        if (jours <= 0) {
            view.afficherCout(0.0)
            return
        }

        view.afficherCout(jours * prixParJour)
    }

    override fun confirmerReservation(
        voitureId: Long,
        disponible: Boolean,
        dateDebut: String?,
        dateFin: String?,
        prixParJour: Double
    ) {
        if (!disponible) {
            view.afficherMessage("Ce vehicule n'est pas disponible a la location.")
            return
        }

        if (dateDebut == null || dateFin == null) {
            view.afficherMessage("Veuillez selectionner une date de debut et une date de fin.")
            return
        }

        val debut = LocalDate.parse(dateDebut)
        val fin = LocalDate.parse(dateFin)
        val jours = ChronoUnit.DAYS.between(debut, fin)

        if (jours <= 0) {
            view.afficherMessage("La date de fin doit etre apres la date de debut.")
            return
        }

        val conflit = reservationRepository.verifierConflit(
            voitureId,
            dateDebut,
            dateFin
        )

        if (conflit) {
            view.afficherMessage("Ce vehicule est deja reserve pour cette periode.")
            return
        }

        reservationRepository.insert(
            Reservation(
                utilisateurId = userSession.getCurrentUserId(),
                voitureId = voitureId,
                dateDebut = dateDebut,
                dateFin = dateFin,
                coutTotal = jours * prixParJour,
                statut = "ACTIVE"
            )
        )

        view.reservationConfirmee()
    }
}
