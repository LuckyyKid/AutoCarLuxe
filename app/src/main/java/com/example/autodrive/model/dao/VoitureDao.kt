package com.example.autodrive.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.autodrive.model.entity.Voiture

@Dao
interface VoitureDao {

    @Insert
    suspend fun insert(voiture: Voiture)

    @Query("SELECT * FROM voiture")
    suspend fun getAll(): List<Voiture>

    @Query("SELECT * FROM voiture WHERE marque LIKE :marque")
    suspend fun rechercherParMarque(marque: String): List<Voiture>

    @Query("SELECT * FROM voiture WHERE prixParJour BETWEEN :min AND :max")
    suspend fun filtrerParPrix(min: Double, max: Double): List<Voiture>

    @Query("SELECT * FROM voiture WHERE estDisponible = 1")
    suspend fun getDisponibles(): List<Voiture>
}