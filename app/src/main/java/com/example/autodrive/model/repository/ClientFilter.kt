package com.example.autodrive.model.repository

data class ClientFilter(
    val recherche: String,
    val marque: String,
    val modele: String,
    val prixMin: String,
    val prixMax: String,
    val annee: String,
    val seulementDisponibles: Boolean
)
