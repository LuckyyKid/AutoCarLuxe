package com.example.autodrive.model.repository

import android.content.Context
import com.example.autodrive.model.AppDatabase
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.ReservationWithVoiture

class ReservationRepository(context: Context) {

    private val reservationDao = AppDatabase.getDatabase(context).reservationDao()

    fun insert(reservation: Reservation) {
        reservationDao.insert(reservation)
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
}
