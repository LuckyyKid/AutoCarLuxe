package com.example.autodrive.presenter

import com.example.autodrive.model.entity.ReservationWithVoiture
import com.example.autodrive.model.repository.ReservationRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.contract.MesReservationsContract
import java.time.LocalDate

class MesReservationsPresenter(
    private val view: MesReservationsContract.View,
    private val reservationRepository: ReservationRepository,
    private val userSession: UserSession
) : MesReservationsContract.Presenter {

    override fun chargerReservations() {
        val userId = userSession.getCurrentUserId()
        val aujourdHui = LocalDate.now()

        val reservations = reservationRepository.getHistoriqueAvecVoiture(userId).map { reservation ->
            mettreAJourStatutSiBesoin(reservation, aujourdHui)
        }

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

    private fun mettreAJourStatutSiBesoin(
        reservation: ReservationWithVoiture,
        aujourdHui: LocalDate
    ): ReservationWithVoiture {
        if (reservation.statut == "ACTIVE" && LocalDate.parse(reservation.dateFin).isBefore(aujourdHui)) {
            reservationRepository.terminerReservation(reservation.id)
            return reservation.copy(statut = "TERMINEE")
        }

        return reservation
    }
}
