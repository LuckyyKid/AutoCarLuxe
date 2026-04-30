package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.autodrive.model.entity.Voiture

@Dao
interface VoitureDao {

    @Insert
    fun insert(voiture: Voiture)

    @Update
    fun update(voiture: Voiture)

    @Query("SELECT * FROM voiture")
    fun getAll(): List<Voiture>

    @Query("SELECT * FROM voiture ORDER BY prixParJour ASC")
    fun getAllOrderByPrix(): List<Voiture>

    @Query("SELECT * FROM voiture WHERE id = :id LIMIT 1")
    fun getById(id: Long): Voiture?

    @Query("DELETE FROM voiture WHERE id = :id")
    fun deleteById(id: Long)

    @Query("UPDATE voiture SET estDisponible = :dispo WHERE id = :id")
    fun updateDisponibilite(id: Long, dispo: Boolean)

    @Query("""
        SELECT * FROM voiture
        WHERE marque LIKE '%' || :recherche || '%'
        OR modele LIKE '%' || :recherche || '%'
    """)
    fun rechercherVoitures(recherche: String): List<Voiture>

    @Query("SELECT * FROM voiture WHERE marque = :marque")
    fun filtrerParMarque(marque: String): List<Voiture>

    @Query("SELECT * FROM voiture WHERE prixParJour BETWEEN :min AND :max")
    fun filtrerParPrix(min: Double, max: Double): List<Voiture>

    @Query("SELECT * FROM voiture WHERE estDisponible = 1")
    fun getDisponibles(): List<Voiture>
}
