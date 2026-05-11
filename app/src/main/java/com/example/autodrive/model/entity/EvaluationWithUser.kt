package com.example.autodrive.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EvaluationWithUser(
    @Embedded val evaluation: Evaluation,
    @Relation(
        parentColumn = "utilisateurId",
        entityColumn = "id"
    )
    val utilisateur: Utilisateur
)
