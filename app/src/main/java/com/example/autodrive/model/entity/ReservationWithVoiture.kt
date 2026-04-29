package com.example.autodrive.model.entity

data class ReservationWithVoiture(
    val id: Long,
    val voitureId: Long,
    val marque: String,
    val modele: String,
    val dateDebut: String,
    val dateFin: String,
    val coutTotal: Double,
    val statut: String
)