package com.example.autodrive.presenter.contract

interface ReservationContract {

    interface View {
        fun afficherCout(coutTotal: Double)
        fun afficherMessage(message: String)
        fun reservationConfirmee()
    }

    interface Presenter {
        fun initialiserUtilisateur()
        fun calculerCout(dateDebut: String?, dateFin: String?, prixParJour: Double)
        fun confirmerReservation(
            voitureId: Long,
            disponible: Boolean,
            dateDebut: String?,
            dateFin: String?,
            prixParJour: Double
        )
    }
}
