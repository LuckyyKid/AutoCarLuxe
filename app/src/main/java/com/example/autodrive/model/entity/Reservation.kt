package com.example.autodrive.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservation",
    foreignKeys = [
        ForeignKey(
            entity = Voiture::class,
            parentColumns = ["id"],
            childColumns = ["voitureId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["id"],
            childColumns = ["utilisateurId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val utilisateurId: Long,
    val voitureId: Long,
    val dateDebut: String,
    val dateFin: String,
    val coutTotal: Double,
    val statut: String
)