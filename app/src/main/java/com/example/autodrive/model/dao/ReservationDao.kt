package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.autodrive.model.entity.Reservation
import com.example.autodrive.model.entity.ReservationWithVoiture

@Dao
interface ReservationDao {

    @Insert
    fun insert(reservation: Reservation)

    @Update
    fun update(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

    @Query("SELECT * FROM reservation")
    fun getAll(): List<Reservation>

    @Query("SELECT * FROM reservation WHERE id = :id LIMIT 1")
    fun getById(id: Long): Reservation?

    @Query("""
        SELECT r.id, r.voitureId, v.marque, v.modele,
               r.dateDebut, r.dateFin, r.coutTotal, r.statut
        FROM reservation r
        INNER JOIN voiture v ON r.voitureId = v.id
        WHERE r.utilisateurId = :userId
        ORDER BY r.dateDebut DESC
    """)
    fun getHistoriqueAvecVoiture(userId: Long): List<ReservationWithVoiture>

    @Query("UPDATE reservation SET statut = 'ANNULEE' WHERE id = :id")
    fun annulerReservation(id: Long)

    @Query("UPDATE reservation SET statut = 'TERMINEE' WHERE id = :id")
    fun terminerReservation(id: Long)

    @Query("""
        SELECT * FROM reservation
        WHERE voitureId = :voitureId
        AND statut = 'ACTIVE'
        AND dateDebut < :dateFin
        AND dateFin  > :dateDebut
    """)
    fun verifierConflit(
        voitureId: Long,
        dateDebut: String,
        dateFin: String
    ): List<Reservation>

    @Query("SELECT COUNT(*) FROM reservation WHERE utilisateurId = :userId")
    fun countReservations(userId: Long): Int

    @Query("SELECT SUM(coutTotal) FROM reservation WHERE utilisateurId = :userId")
    fun totalDepense(userId: Long): Double?
}
