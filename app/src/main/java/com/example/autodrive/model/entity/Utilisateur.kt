package com.example.autodrive.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utilisateur")
data class Utilisateur(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String,
    val prenom: String,
    val email: String,
    val motDePasseHash: String,
    val dateNaissance: String
)