package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.ReservationWithVoiture
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ReservationRepository(context: Context) {

    private val reservationDao = AppDatabase.getDatabase(context).reservationDao()

    fun insert(reservation: Reservation) {
        reservationDao.insert(reservation)
    }

    fun update(reservation: Reservation) {
        reservationDao.update(reservation)
    }

    fun delete(reservation: Reservation) {
        reservationDao.delete(reservation)
    }

    fun getAll(): List<Reservation> {
        return reservationDao.getAll()
    }

    fun getById(id: Long): Reservation? {
        return reservationDao.getById(id)
    }

    fun getHistoriqueAvecVoiture(userId: Long): List<ReservationWithVoiture> {
        return reservationDao.getHistoriqueAvecVoiture(userId)
    }

    fun annulerReservation(id: Long) {
        reservationDao.annulerReservation(id)
    }

    fun terminerReservation(id: Long) {
        reservationDao.terminerReservation(id)
    }

    fun verifierConflit(
        voitureId: Long,
        dateDebut: String,
        dateFin: String
    ): Boolean {
        return reservationDao.verifierConflit(voitureId, dateDebut, dateFin).isNotEmpty()
    }

    fun countReservations(userId: Long): Int {
        return reservationDao.countReservations(userId)
    }

    fun totalDepense(userId: Long): Double {
        return reservationDao.totalDepense(userId) ?: 0.0
    }

    fun calculerCout(dateDebut: String?, dateFin: String?, prixParJour: Double): Double {
        if (dateDebut == null || dateFin == null) {
            return 0.0
        }

        val debut = LocalDate.parse(dateDebut)
        val fin = LocalDate.parse(dateFin)
        val jours = ChronoUnit.DAYS.between(debut, fin)

        if (jours <= 0) {
            return 0.0
        }

        return jours * prixParJour
    }

    fun creerReservation(
        userId: Long,
        voitureId: Long,
        disponible: Boolean,
        dateDebut: String?,
        dateFin: String?,
        prixParJour: Double
    ): ReservationCreationResult {
        if (!disponible) {
            return ReservationCreationResult(
                succes = false,
                message = "Ce vehicule n'est pas disponible a la location."
            )
        }

        if (dateDebut == null || dateFin == null) {
            return ReservationCreationResult(
                succes = false,
                message = "Veuillez selectionner une date de debut et une date de fin."
            )
        }

        val cout = calculerCout(dateDebut, dateFin, prixParJour)
        if (cout <= 0.0) {
            return ReservationCreationResult(
                succes = false,
                message = "La date de fin doit etre apres la date de debut."
            )
        }

        if (verifierConflit(voitureId, dateDebut, dateFin)) {
            return ReservationCreationResult(
                succes = false,
                message = "Ce vehicule est deja reserve pour cette periode."
            )
        }

        insert(
            Reservation(
                utilisateurId = userId,
                voitureId = voitureId,
                dateDebut = dateDebut,
                dateFin = dateFin,
                coutTotal = cout,
                statut = "ACTIVE"
            )
        )

        return ReservationCreationResult(succes = true)
    }

    fun getHistoriqueAvecStatutsMisAJour(userId: Long): List<ReservationWithVoiture> {
        val aujourdHui = LocalDate.now()

        return getHistoriqueAvecVoiture(userId).map { reservation ->
            if (reservation.statut == "ACTIVE" && LocalDate.parse(reservation.dateFin).isBefore(aujourdHui)) {
                terminerReservation(reservation.id)
                reservation.copy(statut = "TERMINEE")
            } else {
                reservation
            }
        }
    }
}
