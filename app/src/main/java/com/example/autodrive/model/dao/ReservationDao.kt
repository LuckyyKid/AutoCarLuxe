package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.autodrive.model.entity.Reservation


@Dao
interface ReservationDao {

    @Insert
     fun insert(reservation: Reservation)

    @Query("SELECT * FROM reservation WHERE utilisateurId = :userId")
     fun getReservationsUtilisateur(userId: Long): List<Reservation>

    @Query("DELETE FROM reservation WHERE id = :id")
    fun supprimerReservation(id: Long)

    @Query("""
        SELECT * FROM reservation 
        WHERE voitureId = :voitureId 
        AND (
            dateDebut < :dateFin AND dateFin > :dateDebut
        )
    """)
     fun verifierConflit(
        voitureId: Long,
        dateDebut: String,
        dateFin: String
    ): List<Reservation>
}