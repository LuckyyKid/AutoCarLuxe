package com.example.autodrive.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "voiture")
data class Voiture(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marque: String,
    val modele: String,
    val annee: Int,
    val prixParJour: Double,
    val estDisponible: Boolean,
    val imageUrls: String?,
    val description: String?,
    val ageMinimum: Int
) : Serializable