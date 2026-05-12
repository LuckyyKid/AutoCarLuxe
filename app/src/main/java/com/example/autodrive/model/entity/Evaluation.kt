package com.example.autodrive.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evaluation",
    indices = [
        Index(value = ["utilisateurId"]),
        Index(value = ["voitureId"])
    ],
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
data class Evaluation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val utilisateurId: Long,
    val voitureId: Long,
    val note: Float,
    val commentaire: String?,
    val dateEvaluation: String
)
