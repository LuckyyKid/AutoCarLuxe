package com.example.autodrive.presenter

import com.example.autodrive.model.entity.ReservationWithVoiture
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.MesReservationsContract

class MesReservationsPresenter(
    private val view: MesReservationsContract.View,
    private val reservationRepository: ReservationRepository,
    private val userSession: UserSession
) : MesReservationsContract.Presenter {

    private var toutesReservations: List<ReservationWithVoiture> = emptyList()
    private var filtreStatut: String = "TOUS"

    override fun chargerReservations() {
        val userId = userSession.getCurrentUserId()
        toutesReservations = reservationRepository.getHistoriqueAvecStatutsMisAJour(userId)

        appliquerFiltre()
        view.afficherStatistiques(
            reservationRepository.totalDepense(userId),
            reservationRepository.countReservations(userId)
        )
    }

    override fun changerFiltre(statut: String) {
        filtreStatut = statut
        appliquerFiltre()
    }

    override fun annulerReservation(id: Long) {
        reservationRepository.annulerReservation(id)
        chargerReservations()
    }

    private fun appliquerFiltre() {
        view.afficherFiltre(filtreStatut)
        view.afficherReservations(
            if (filtreStatut == "TOUS") {
                toutesReservations
            } else {
                toutesReservations.filter { it.statut == filtreStatut }
            }
        )
    }
}
