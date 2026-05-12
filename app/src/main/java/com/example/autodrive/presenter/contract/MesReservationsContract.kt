package com.example.autodrive.presenter.contract

import com.example.autodrive.model.entity.ReservationWithVoiture

interface MesReservationsContract {

    interface View {
        fun afficherReservations(reservations: List<ReservationWithVoiture>)
        fun afficherStatistiques(total: Double, count: Int)
        fun afficherFiltre(statut: String)
    }

    interface Presenter {
        fun chargerReservations()
        fun changerFiltre(statut: String)
        fun annulerReservation(id: Long)
    }
}
