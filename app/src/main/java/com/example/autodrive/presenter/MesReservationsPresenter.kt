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

    override fun chargerReservations() {
        val userId = userSession.getCurrentUserId()
        val reservations = reservationRepository.getHistoriqueAvecStatutsMisAJour(userId)

        view.afficherReservations(reservations)
        view.afficherStatistiques(
            reservationRepository.totalDepense(userId),
            reservationRepository.countReservations(userId)
        )
    }

    override fun annulerReservation(id: Long) {
        reservationRepository.annulerReservation(id)
        chargerReservations()
    }
}
